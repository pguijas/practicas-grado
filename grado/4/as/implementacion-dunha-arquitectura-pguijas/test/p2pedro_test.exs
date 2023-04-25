defmodule P2pedroTest do
  use ExUnit.Case
  doctest P2pedro

  test "creating network" do
    {:ok, node} = P2pedro.up_node(1)
    {:ok, node1} = P2pedro.up_node(node, 2)
    {:ok, node2} = P2pedro.up_node(node1, 3)

    assert P2pedro.get_next_node(node) == node1
    assert P2pedro.get_next_node(node1) == node2
    assert P2pedro.get_next_node(node2) == node

    assert P2pedro.get_previous_node(node) == node2
    assert P2pedro.get_previous_node(node2) == node1
    assert P2pedro.get_previous_node(node1) == node
  end

  test "removing nodes" do
    {:ok, node} = P2pedro.up_node(1)
    {:ok, node1} = P2pedro.up_node(node, 2)
    {:ok, node2} = P2pedro.up_node(node1, 3)

    P2pedro.down_node(node2)

    assert P2pedro.get_next_node(node) == node1
    assert P2pedro.get_next_node(node1) == node

    assert P2pedro.get_previous_node(node) == node1
    assert P2pedro.get_previous_node(node1) == node

    P2pedro.down_node(node1)
    assert P2pedro.get_next_node(node) == node
    assert P2pedro.get_previous_node(node) == node
  end

  test "merging network" do
    {:ok, node} = P2pedro.up_node(1)
    {:ok, node1} = P2pedro.up_node(node, 2)
    {:ok, node2} = P2pedro.up_node(node1, 3)

    P2pedro.start_merge(node)

    Process.sleep(100)

    assert P2pedro.get_status(node) == 2
    assert P2pedro.get_status(node1) == 2
    assert P2pedro.get_status(node2) == 2
  end
end
