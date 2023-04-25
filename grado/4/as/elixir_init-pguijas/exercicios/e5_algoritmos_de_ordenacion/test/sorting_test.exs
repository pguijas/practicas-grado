defmodule SortingTest do
  use ExUnit.Case, async: true
  use ExUnitProperties
  doctest Sorting

  property "quicksort ordea calquera lista de elementos" do
    check all(lista <- list_of(term())) do
      assert Sorting.quicksort(lista) == Enum.sort(lista)
    end
  end

  property "mergesort ordea calquera lista de elementos" do
    check all(lista <- list_of(term())) do
      assert Sorting.mergesort(lista) == Enum.sort(lista)
    end
  end
end
