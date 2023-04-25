defmodule EchoTest do
  use ExUnit.Case
  doctest Echo

  use PropCheck
  use PropCheck.StateM

  property "o servidor de echo imprime todo o que se lle manda", [:verbose] do
    forall cmds in commands(__MODULE__) do
      trap_exit do
        kill_echo_if_alive()
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

  defp kill_echo_if_alive() do
    echo = Process.registered() |> Enum.find(fn x -> x == :echo end)

    if not is_nil(echo) do
      ref = Process.monitor(echo)
      pid = Process.whereis(echo)

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

  ### State machine

  defp mensaxe() do
    oneof([
      "Hola cara de bola",
      "No te aburres de estos mensajes absurdos?",
      "Hasta la vista, baby!",
      "No contaban con mi astucia...",
      "Enough is enough"
    ])
  end

  def command(:stopped), do: {:call, Echo, :start, []}

  def command(:started) do
    oneof([
      {:call, __MODULE__, :print, [mensaxe()]},
      {:call, Echo, :stop, []}
    ])
  end

  def initial_state(), do: :stopped

  def next_state(:stopped, _, {:call, Echo, :start, []}), do: :started
  def next_state(:started, _, {:call, Echo, :stop, []}), do: :stopped
  def next_state(state, _, _), do: state

  def precondition(:started, {:call, Echo, :start, []}), do: false
  def precondition(:stopped, {:call, __MODULE__, :print, [_]}), do: false
  def precondition(:stopped, {:call, Echo, :stop, []}), do: false
  def precondition(_, _), do: true

  def postcondition(:started, {:call, __MODULE__, :print, [mensaxe]}, mensaxe), do: true
  def postcondition(:started, {:call, __MODULE__, :print, [_]}, _), do: false
  def postcondition(_, _, _), do: true

  def print(mensaxe) do
    Process.group_leader(Process.whereis(:echo), self())
    Echo.print(mensaxe)

    receive do
      {:io_request, from, reply_as, {:put_chars, _, msg}} ->
        send(from, {:io_reply, reply_as, :ok})
        String.replace_suffix(msg, "\n", "")

      _ ->
        "Mensaxe non agardada no proceso echo"
    end
  end
end
