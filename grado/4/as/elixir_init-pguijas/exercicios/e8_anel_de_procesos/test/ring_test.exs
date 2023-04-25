defmodule RingTest do
  use ExUnit.Case
  use ExUnitProperties
  doctest Ring

  property "un anel de calquera tamaño compórtase dacordo coa especificación" do
    check all(
            n <- positive_integer(),
            m <- positive_integer(),
            msx <- mensaxe(),
            max_run_time: 10000
          ) do
      # IO.puts("Probando con #{inspect(n+1)} procesos e #{inspect(m)} mensaxes #{inspect(msx)}")
      tracer = start_tracing()
      # dámoslle temo ao tracer a poñerse a funcionar
      Process.sleep((n + 1) * m * 10)

      Ring.start(n + 1, m, msx)

      # agardamos a que o anel se cree, envíe, e destrúa
      Process.sleep((n + 1) * m * 10)
      trazas = stop_tracing(tracer)
      # IO.puts("Trazas: #{inspect(trazas)}")

      # filtramos a información necesaria
      children = creacion(trazas)
      senders_and_receivers = envio(msx, trazas)
      receivers = recepcion(msx, trazas)
      exited = destruccion(trazas)

      # facemos as comprobacións pertinentes      
      assert length(children) == n + 1, "non se crearon o número de procesos indicado"
      assert length(senders_and_receivers) == m, "non se enviaron o núm de mensaxes indicado"
      assert length(receivers) == m, "non se recibiu o número de mensaxes agardado"
      assert length(exited) == n + 1, "non se eliminaron todos os procesos creados"
    end
  end

  defp mensaxe() do
    ExUnitProperties.gen all(
                           cadea <-
                             member_of([
                               "Hola cara de bola",
                               "No te aburres de estos mensajes absurdos?",
                               "Hasta la vista, baby!",
                               "No contaban con mi astucia...",
                               "Enough is enough"
                             ])
                         ) do
      cadea
    end
  end

  defp start_tracing do
    tracer = spawn(fn -> tracer() end)
    # habilitamos as trazas
    :erlang.trace(:all, true, [:procs, :send, :receive, {:tracer, tracer}])
    tracer
  end

  defp stop_tracing(tracer) do
    # deshabilitamos as trazas
    :erlang.trace(:all, false, [:all])
    # recollemos as trazas do proceso tracer
    send(tracer, {:collect, self()})

    receive do
      {trazas, ^tracer} -> trazas
    end
  end

  defp tracer(), do: recoller_trazas([])

  defp recoller_trazas(trazas) do
    receive do
      {:collect, from} ->
        send(from, {Enum.reverse(trazas), self()})

      nova_traza ->
        recoller_trazas([nova_traza | trazas])
    end
  end

  defp creacion(trazas) do
    for {:trace, child, :spawned, _parent, _fun} <- trazas, do: child
  end

  defp envio(msg, trazas) do
    for {:trace, _from, :send, {_, ^msg}, _to} <- trazas, do: msg
  end

  defp recepcion(msg, trazas) do
    for {:trace, _process, :receive, {_, ^msg}} <- trazas, do: msg
  end

  defp destruccion(trazas) do
    for {:trace, process, :exit, :normal} <- trazas, do: process
  end
end
