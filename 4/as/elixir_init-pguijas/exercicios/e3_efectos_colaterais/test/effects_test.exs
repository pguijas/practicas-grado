defmodule EffectsTest do
  use ExUnit.Case, async: true
  use ExUnitProperties
  import ExUnit.CaptureIO
  doctest Effects

  property "pódese imprimir calquera lista de enteiros positivos" do
    check all(enteiro <- positive_integer()) do
      assert capture_io(fn -> Effects.print(enteiro) end) ==
               Enum.join(Enum.to_list(1..enteiro), "\n") <> "\n"
    end
  end

  property "pódense imprimir os pares de calquera lista de enteiros positivos" do
    check all(enteiro <- integer_greater_than_one()) do
      assert capture_io(fn -> Effects.even_print(enteiro) end) ==
               (Enum.filter(
                  1..enteiro,
                  fn x -> rem(x, 2) == 0 end
                )
                |> Enum.to_list()
                |> Enum.join("\n")) <> "\n"
    end
  end

  defp integer_greater_than_one() do
    ExUnitProperties.gen all(enteiro <- positive_integer()) do
      enteiro + 1
    end
  end
end
