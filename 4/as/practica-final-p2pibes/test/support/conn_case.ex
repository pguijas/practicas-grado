defmodule P2pibesWeb.ConnCase do
  @moduledoc false

  use ExUnit.CaseTemplate

  using do
    quote do
      # Import conveniences for testing with connections
      import Plug.Conn
      import Phoenix.ConnTest
      import P2pibesWeb.ConnCase

      alias P2pibesWeb.Router.Helpers, as: Routes

      # The default endpoint for testing
      @endpoint P2pibesWeb.Endpoint
    end
  end

  setup _tags do
    {:ok, conn: Phoenix.ConnTest.build_conn()}
  end
end
