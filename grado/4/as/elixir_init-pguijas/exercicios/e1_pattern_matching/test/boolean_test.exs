defmodule BooleanTest do
  use ExUnit.Case, async: true
  doctest Boolean

  test "negación de false" do
    assert Boolean.b_not(false)
  end

  test "conxunción de dous verdadeiros" do
    assert Boolean.b_and(true, true)
  end

  test "conxunción de dous falsos" do
    refute Boolean.b_and(false, false)
  end

  test "conxunción dun verdadeiro e un falso" do
    refute Boolean.b_and(true, false)
  end

  test "conxunción dun falso e un verdadeiro" do
    refute Boolean.b_and(false, true)
  end

  test "disxunción de dous verdadeiros" do
    assert Boolean.b_or(true, true)
  end

  test "disxunción de dous falsos" do
    refute Boolean.b_or(false, false)
  end

  test "disxunción dun verdadeiro e un falso" do
    assert Boolean.b_or(true, false)
  end

  test "disxunción dun falso e un verdadeiro" do
    assert Boolean.b_or(false, true)
  end
end
