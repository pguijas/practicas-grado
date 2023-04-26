defmodule AprendendoElixir.MixProject do
  use Mix.Project

  def project do
    [
      name: "Aprendendo Elixir",
      apps_path: "exercicios",
      start_permanent: Mix.env() == :prod,
      deps: deps(),
      dialyzer: [plt_add_deps: :transitive]
    ]
  end

  # Run "mix help deps" for examples and options.
  defp deps do
    [
      {:earmark, "~> 1.4", only: [:dev]},
      {:ex_doc, "~> 0.23", only: [:dev]},
      {:dialyxir, "~> 1.0", only: [:dev]},
      {:stream_data, "~>0.5", only: [:test]}
    ]
  end
end
