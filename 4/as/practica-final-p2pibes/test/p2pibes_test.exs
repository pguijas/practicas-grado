defmodule P2pibesTest do
  use ExUnit.Case
  doctest P2pibes

  setup do
    P2pibes.up_node(:node1)
    P2pibes.up_node(:node2, :node1)
    P2pibes.up_node(:node3, :node2)
    P2pibes.up_node(:node4, :node3)
    P2pibes.up_node(:node5, :node4)
    P2pibes.up_node(:node6, :node5)
    P2pibes.up_node(:node7, :node6)
    P2pibes.up_node(:node8)
    :ok
  end

  test "Creacion de nodos" do
    assert P2pibes.get_neighbors(:node1) == [:node2]
    assert P2pibes.get_neighbors(:node2) == [:node3, :node1]
    assert P2pibes.get_neighbors(:node3) == [:node4, :node2]
    assert P2pibes.get_neighbors(:node4) == [:node5, :node3]
    assert P2pibes.get_neighbors(:node5) == [:node6, :node4]
    assert P2pibes.get_neighbors(:node6) == [:node7, :node5]
    assert P2pibes.get_neighbors(:node7) == [:node6]
    assert P2pibes.get_neighbors(:node8) == []
  end

  test "Destruccion de nodos" do
    assert P2pibes.get_neighbors(:node1) == [:node2]
    assert P2pibes.get_neighbors(:node3) == [:node4, :node2]
    pid = Process.whereis(:node2)
    P2pibes.down_node(:node2)
    assert Process.alive?(pid) == false
    assert P2pibes.get_neighbors(:node1) == []
    assert P2pibes.get_neighbors(:node3) == [:node4]
  end

  test "Añadido de archivos" do
    P2pibes.add_file(:node1, "fichero_inexistente.txt")
    assert P2pibes.get_files(:node1) == []
    P2pibes.add_file(:node1, "mix.exs")
    :timer.sleep(100)
    assert 1 == Enum.count(P2pibes.get_files(:node1))
    P2pibes.add_file(:node1, "README.md")
    :timer.sleep(100)
    assert 2 == Enum.count(P2pibes.get_files(:node1))
    element = hd(P2pibes.get_files(:node1))
    assert element[:path] == Path.expand("README.md")
  end

  test "Añadido de directorios" do
    P2pibes.add_file(:node1, "test/prueba_añadido")
    :timer.sleep(100)
    fichero_carpeta = hd(P2pibes.get_files(:node1))
    fichero = hd(tl(P2pibes.get_files(:node1)))
    assert 2 == Enum.count(P2pibes.get_files(:node1))
    assert fichero[:path] == Path.expand("test/prueba_añadido/fichero1.txt")

    assert fichero_carpeta[:path] ==
             Path.expand("test/prueba_añadido/prueba_recursividad/fichero1.txt")
  end

  test "Borrado de archivos" do
    P2pibes.add_file(:node1, "mix.exs")
    P2pibes.add_file(:node1, "README.md")
    :timer.sleep(100)
    assert 2 == Enum.count(P2pibes.get_files(:node1))
    P2pibes.rm_file(:node1, "README.md")
    :timer.sleep(100)
    assert 1 == Enum.count(P2pibes.get_files(:node1))
    element = hd(P2pibes.get_files(:node1))
    assert element[:path] == Path.expand("mix.exs")
  end

  test "Borrado de directorios" do
    P2pibes.add_file(:node1, "test/prueba_añadido")
    :timer.sleep(100)
    assert 2 == Enum.count(P2pibes.get_files(:node1))
    P2pibes.add_file(:node1, "mix.exs")
    :timer.sleep(100)
    assert 3 == Enum.count(P2pibes.get_files(:node1))
    P2pibes.rm_file(:node1, "test/prueba_añadido")
    :timer.sleep(100)
    assert 1 == Enum.count(P2pibes.get_files(:node1))
    element = hd(P2pibes.get_files(:node1))
    assert element[:path] == Path.expand("mix.exs")
  end

  test "Busqueda de archivos" do
    P2pibes.add_file(:node2, "test/pruebas/peer2/peer2.txt")
    P2pibes.add_file(:node3, "test/pruebas/peer2/peer2.txt")
    assert P2pibes.get_results(:node1, "peer2.txt") == []
    P2pibes.search(:node1, "peer2.txt")
    :timer.sleep(100)
    node2 = hd(P2pibes.get_results(:node1, "peer2.txt"))
    node3 = hd(tl(P2pibes.get_results(:node1, "peer2.txt")))
    assert node2[:node] == :node2
    assert node3[:node] == :node3
    assert node2[:name] == "peer2.txt"
    assert node3[:name] == "peer2.txt"
  end

  test "Busqueda de archivos ttl" do
    P2pibes.add_file(:node7, "test/pruebas/peer2/peer2.txt")
    assert P2pibes.get_results(:node1, "peer2.txt") == []
    P2pibes.search(:node1, "peer2.txt")
    :timer.sleep(100)
    assert P2pibes.get_results(:node1, "peer2.txt") == []
    P2pibes.add_file(:node6, "test/pruebas/peer1/peer1.txt")
    P2pibes.search(:node1, "peer1.txt")
    :timer.sleep(100)
    assert P2pibes.get_results(:node1, "peer1.txt") != []
  end
end
