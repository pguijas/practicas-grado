defmodule ManipulatingTest do
  use ExUnit.Case, async: true
  use ExUnitProperties
  doctest Manipulating

  property "pódese filtrar calquera lista de enteiros por calquera enteiro" do
    check all(
            enteiro <- integer(),
            lista <- list_of(integer())
          ) do
      assert Manipulating.filter(lista, enteiro) == Enum.filter(lista, fn x -> x <= enteiro end)
    end
  end

  property "pódese dar a volta a calquera lista de elementos" do
    check all(lista <- list_of(term())) do
      assert Manipulating.reverse(lista) == Enum.reverse(lista)
    end
  end

  property "pódese concatenar calquera lista de listas" do
    check all(lista <- list_of(list_of(term())), max_run_time: 10000) do
      assert Manipulating.concatenate(lista) == Enum.concat(lista)
    end
  end

  property "pódese aplanar calquera lista de listas de listas" do
    check all(lista <- nested_list(), max_run_time: 10000) do
      assert Manipulating.flatten(lista) == List.flatten(lista)
    end
  end

  defp nested_list() do
    ExUnitProperties.gen all(
                           lista <-
                             list_of(
                               one_of([
                                 term(),
                                 list_of(term()),
                                 list_of(list_of(term()))
                               ])
                             )
                         ) do
      lista
    end
  end
end
