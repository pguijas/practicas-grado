import Config

# We don't run a server during test. If one is required,
# you can enable the server option below.
config :p2pibes, P2pibesWeb.Endpoint,
  http: [ip: {127, 0, 0, 1}, port: 4002],
  secret_key_base: "L+mI/THBNE+UNB+bRDcu25hSIH2hK7Oxx69VdOk9OL1QcLxOFG5JOFsEOmujwlxl",
  server: false

# In test we don't send emails.
config :p2pibes, P2pibes.Mailer, adapter: Swoosh.Adapters.Test

# Print only warnings and errors during test
config :logger, level: :warn

# Initialize plugs at runtime for faster test compilation
config :phoenix, :plug_init_mode, :runtime
