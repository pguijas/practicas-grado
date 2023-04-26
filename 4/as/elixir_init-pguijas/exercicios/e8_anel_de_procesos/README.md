# Oitavo exercicio: enlazando procesos

Para completar esta tarefa de programación debes crear un módulo
`ring` cunha función pública `start/3`:

```start(n, m, msg)```
```:ok```

Esta función debe crear `N` procesos enlazados en forma de anel y
enviar a mensaxe `Msg` `M` veces, de xeito que percorra o anel
completo. Unha vez enviadas todas as mensaxes, os procesos do anel
deben terminar a súa execución ordenadamente.

## Notas

A mensaxe pode non chegar a todos os procesos do anel, se `M<N`.

Hai dúas estratexias básicas para crear o anel:

+ Un _proceso distinguido_ crea os demais procesos, configura o anel,
e envía a primeira mensaxe.
+ Cada proceso é responsable de crear o seguinte proceso do anel
recursivamente. Neste caso, é preciso establecer un mecanismo para que
o derradeiro proceso do anel sexa quen de conectar co primeiro.