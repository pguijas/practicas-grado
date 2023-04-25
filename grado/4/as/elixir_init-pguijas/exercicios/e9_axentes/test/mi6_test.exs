defmodule Mi6Test do
  use ExUnit.Case
  doctest Mi6

  use PropCheck
  use PropCheck.StateM

  property "a axencia e os seus axentes espían e contraespían", [:verbose] do
    forall cmds in commands(__MODULE__) do
      trap_exit do
        kill_agency_if_alive()
        {history, state, result} = run_commands(__MODULE__, cmds)

        (result == :ok)
        |> when_fail(
          IO.puts("""
          History: #{inspect(history, pretty: true)}
          State: #{inspect(state, pretty: true)}
          Result: #{inspect(result, pretty: true)}
          """)
        )
        |> aggregate(command_names(cmds))
      end
    end
  end

  defp kill_agency_if_alive() do
    agency = Process.registered() |> Enum.find(fn x -> x == :mi6 end)

    if not is_nil(agency) do
      ref = Process.monitor(agency)
      pid = Process.whereis(agency)

      try do
        Process.exit(pid, :kill)
      catch
        _, _ -> :already_killed
      end

      receive do
        {:DOWN, ^ref, :process, _, _} -> :ok
      end
    end
  end

  ## State machine

  defp axente() do
    oneof([:axente006, :axente007, :axente008, :axente009, :axente0011])
  end

  defp destino() do
    oneof(["London", "Paris", "Santiago de Compostela", "Saint Petersburg", "Casablanca"])
  end

  defp mision() do
    oneof([:espiar, :contrainformar])
  end

  def command(:stopped), do: {:call, Mi6, :fundar, []}

  def command({:started, _state}) do
    oneof([
      {:call, Mi6, :recrutar, [axente(), destino()]},
      {:call, Mi6, :asignar_mision, [axente(), mision()]},
      {:call, Mi6, :consultar_estado, [axente()]},
      {:call, Mi6, :disolver, []}
    ])
  end

  def initial_state(), do: :stopped

  def next_state(:stopped, _, {:call, Mi6, :fundar, []}), do: {:started, []}

  def next_state({:started, state}, _, {:call, Mi6, :recrutar, [axente, destino]}) do
    case List.keymember?(state, axente, 0) do
      true -> {:started, state}
      false -> {:started, [{axente, String.length(destino)} | state]}
    end
  end

  def next_state({:started, _}, _, {:call, Mi6, :disolver, []}), do: :stopped
  def next_state(state, _, _), do: state

  def precondition({:started, _}, {:call, Mi6, :fundar, []}), do: false
  def precondition(:stopped, {:call, Mi6, :recrutar, [_, _]}), do: false
  def precondition(:stopped, {:call, Mi6, :asignar_mision, [_, _]}), do: false
  def precondition(:stopped, {:call, Mi6, :consultar_estado, [_]}), do: false
  def precondition(:stopped, {:call, Mi6, :disolver, []}), do: false
  def precondition(_, _), do: true

  def postcondition(
        {:started, state},
        {:call, Mi6, :consultar_estado, [axente]},
        :you_are_here_we_are_not
      ) do
    not List.keymember?(state, axente, 0)
  end

  def postcondition({:started, state}, {:call, Mi6, :consultar_estado, [axente]}, result) do
    {^axente, n} = List.keyfind(state, axente, 0)
    length(result) <= n
  end

  def postcondition(_, _, _), do: true
end
