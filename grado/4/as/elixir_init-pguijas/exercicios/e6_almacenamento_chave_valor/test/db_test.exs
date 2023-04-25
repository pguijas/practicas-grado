defmodule DbTest do
  use ExUnit.Case, async: true
  doctest Db

  setup do
    new_db = Db.new()

    on_exit(fn -> Db.destroy(new_db) end)

    {:ok, db: new_db}
  end

  test "procurar un elemento que non está nunha BD baleira", state do
    assert Db.read(state[:db], 0) == {:error, :not_found}
  end

  test "procurar un elemento que non está nunha BD non baleira", state do
    assert Db.write(state[:db], 5, "elemento")
           |> Db.read(0) == {:error, :not_found}
  end

  test "procurar un elemento que si está nunha BD non baleira", state do
    assert Db.write(state[:db], 5, "elemento")
           |> Db.read(5) == {:ok, "elemento"}
  end

  test "recuperar as chaves dun mesmo elemento nunha BD baleira", state do
    assert Db.match(state[:db], "elemento") == []
  end

  test "recuperar as chaves dun elemento inexistente nunha BD non baleira", state do
    assert Db.write(state[:db], 5, "elemento")
           |> Db.match("outro elemento") == []
  end

  test "recuperar as chaves dun elemento único nunha BD non baleira", state do
    assert Db.write(state[:db], 5, "elemento")
           |> Db.match("elemento") == [5]
  end

  test "recuperar as chaves dun elemento repetido nunha BD non baleira", state do
    assert Db.write(state[:db], 5, "elemento")
           |> Db.write(15, "elemento")
           |> Db.match("elemento")
           |> Enum.sort() == [5, 15]
  end

  test "procurar un elemento eliminado nunha BD que queda baleira", state do
    assert Db.write(state[:db], 5, "elemento")
           |> Db.delete(5)
           |> Db.read(5) == {:error, :not_found}
  end

  test "procurar un elemento eliminado dunha BD que non queda baleira", state do
    assert Db.write(state[:db], 5, "elemento")
           |> Db.write(15, "elemento")
           |> Db.delete(5)
           |> Db.read(5) == {:error, :not_found}
  end

  test "procurar un elemento actualizado nunha BD", state do
    assert Db.write(state[:db], 5, "elemento")
           |> Db.write(5, "outro elemento")
           |> Db.read(5) == {:ok, "outro elemento"}
  end
end
