defmodule CreateTest do
  use ExUnit.Case, async: true
  use ExUnitProperties
  doctest Create

  test "crear lista baleira" do
    assert Create.create(0) == []
  end

  property "pódese crear calquera lista de enteiros positivos" do
    check all(enteiro <- positive_integer()) do
      assert Create.create(enteiro) == Enum.to_list(1..enteiro)
    end
  end

  test "crear lista baleira inversa" do
    assert Create.reverse_create(0) == []
  end

  property "pódese crear calquera lista inversa de enteiros positivos" do
    check all(enteiro <- positive_integer()) do
      assert Create.reverse_create(enteiro) == Enum.to_list(enteiro..1)
    end
  end
end
