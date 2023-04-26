# Exercicio sétimo: respondendo ao eco

A tarefa para esta práctica consiste en crear un módulo `echo` no que
implementes un proceso servidor que, despois de arrincar, permaneza
agardando a recibir mensaxes. Dependendo da mensaxe recibida deberá
imprimila e volver quedar á espera, ou deter a súa execución.

A implementación do módulo debe ocultar aos clientes a creación do
proceso e o paso de mensaxes, polo que o acceso ás funcionalidades do
proceso servidor farase mediante unha interface composta polas
seguintes funcións públicas que se incluirán no propio módulo `echo`:

+ `echo:start() -> :ok` Crea (`spawn`) e rexistra (`Process.register`)
o proceso servidor. O nome co que se debe rexistrar o proceso é `:echo`.

+ `echo:stop() -> :ok` Envía a mensaxe de parada ao servidor.

+ `echo:print(term) -> :ok` Envía unha mensaxe para imprimir ao
servidor.
