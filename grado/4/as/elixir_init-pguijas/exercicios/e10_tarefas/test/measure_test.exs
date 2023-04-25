defmodule MeasureTest do
  use ExUnit.Case
  use ExUnitProperties
  doctest Measure

  property "ao medir N funcións créanse N*2 tarefas" do
    check all(l <- pequena_lista_de_funcions(), n <- large_integer(), max_runs: 8) do
      IO.puts("Probando coas funcións #{inspect(l)} e #{inspect(n)} elementos")
      tracer = start_tracing()
      # dámoslle temo ao tracer a poñerse a funcionar
      Process.sleep(1000)

      Measure.run(l, n)

      # agardamos a que as tarefas se creen, executan e midan
      Process.sleep(1000)
      trazas = stop_tracing(tracer)
      # IO.puts("Trazas: #{inspect(trazas)}")

      # filtramos a información necesaria
      tarefas_creadas = creacion(trazas)
      tarefas_rematadas_ou_interrompidas = destruccion(trazas)

      # facemos as comprobacións pertinentes
      assert length(tarefas_creadas) == length(l) * 2,
             "non se crearon o número de tarefas indicado"

      assert length(tarefas_rematadas_ou_interrompidas) == length(l) * 2,
             "non se eliminaron todas as tarefas creadas"
    end
  end

  defp pequena_lista_de_funcions() do
    scale(lista_de_funcions(), fn size -> min(size, 4) end)
  end

  defp lista_de_funcions() do
    ExUnitProperties.gen all(
                           lista <-
                             nonempty(
                               list_of(
                                 member_of([
                                   {Manipulating, :reverse},
                                   {Manipulating, :flatten},
                                   {Sorting, :quicksort},
                                   {Sorting, :mergesort}
                                 ])
                               )
                             )
                         ) do
      lista
    end
  end

  defp large_integer() do
    scale(positive_integer(), fn size -> :math.pow(4, size) |> trunc end)
  end

  defp start_tracing do
    tracer = spawn(fn -> tracer() end)
    # habilitamos as trazas
    :erlang.trace(:new_processes, true, [:procs, {:tracer, tracer}])
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

  defp destruccion(trazas) do
    for {:trace, process, :exit, _reason} <- trazas, do: process
  end
end
