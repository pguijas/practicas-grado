defmodule P2pibesWeb.ErrorViewTest do
  use P2pibesWeb.ConnCase, async: true

  # Bring render/3 and render_to_string/3 for testing custom views
  import Phoenix.View

  test "renders 404.html" do
    assert render_to_string(P2pibesWeb.ErrorView, "404.html", []) == "Not Found"
  end

  test "renders 500.html" do
    assert render_to_string(P2pibesWeb.ErrorView, "500.html", []) == "Internal Server Error"
  end
end
