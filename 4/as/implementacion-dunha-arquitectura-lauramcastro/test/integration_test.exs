defmodule IntegrationTest do
  use ExUnit.Case

  setup do
    Dbus.start_link([])
    Correo.start_link([])

    on_exit(fn ->
      try do
        Dbus.stop()
      catch
        :exit, {:noproc, _} -> :ok
      end

      try do
        Correo.stop()
      catch
        :exit, {:noproc, _} -> :ok
      end
    end)
  end

  test "validates the integration of Correo and Dbus" do
    # Correo takes at most 5_000 to receive _some_ number of emails
    Process.sleep(5_000)
    # so, after that time, there _must_ be some notification on the Dbus
    refute Dbus.get(:unread_emails) == {:ok, nil}
    # and now, if we read them, the notification should dissappear
    Correo.ler_pendentes()
    # this is to yield execution to Dbus
    Process.sleep(1)
    assert Dbus.get(:unread_emails) == {:ok, 0}
  end
end
