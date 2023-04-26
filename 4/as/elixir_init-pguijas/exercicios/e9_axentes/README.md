# Noveno exercicio: recrutando axentes

A tarefa para esta práctica consiste en crear un módulo `mi6` no que
implementes, usando o behavior `GenServer`, un proceso servidor que
leve conta dunha serie de axentes (`Agents`) que manipularán listas de
datos segundo se lles indique a través do proceso servidor.

Por suposto, a implementación debe ocultar aos clientes a existencia
do `GenServer` e dos `Agents`, polo que o acceso ás funcionalidades
descritas farase mediante unha interface composta polas seguintes
funcións públicas implementadas no módulo `mi6`:

+ `mi6:fundar() -> :ok` Crea e rexistra (co nome `:mi6`) o proceso
  servidor.

+ `mi6:recrutar(axente, destino) -> :ok` Chamada asíncrona que creará
  un novo `Agent` (identificado univocamente co átomo `axente`). O
  proceso servidor usará o almacenamento chave-valor definido no
  [exercicio
  6](https://bitbucket.org/lauramcastro/practicas-elixir/src/master/exercicios/e6_almacenamento_chave_valor/)
  para xestionar estes axentes. Os axentes serán inicializados cunha
  lista de lonxitude igual á do seu `destino` (empregando a función
  definida no [exercicio
  2](https://bitbucket.org/lauramcastro/practicas-elixir/src/master/exercicios/e2_creacion_de_listas/)),
  barallada.  Por exemplo: `mi6:recrutar(:superaxente86,
  "Washington")` creará un axente cunha lista de 10 enteiros,
  barallada.

+ `mi6:asignar_mision(axente, mision) -> :ok` Chamada asíncrona que asignará unha
  misión a un axente, no caso de estar recrutado. As misións
  posibles son `:espiar` e `:contrainformar`. Un axente asignado a
  espiar, deberá filtrar a súa lista empregando a cabeceira da
  mesma. Un axente asignado a contrainformar, deberá darlle a
  volta. En ambos casos empregaranse as funcións do [exercicio
  4](https://bitbucket.org/lauramcastro/practicas-elixir/src/master/exercicios/e4_manipulacion_de_listas/).

+ `mi6:consultar_estado(axente) -> lista | :you_are_here_we_are_not`
  Chamada síncrona que devolverá a lista custodiada polo axente
  indicado. No caso de que na axencia non se teña constancia de tal
  axente, a resposta será o átomo `:you_are_here_we_are_not`.

+ `mi6:disolver() -> :ok` Detén o proceso servidor, quen
  respectivamente debe facer o propio cos axentes que teña recrutados.

## Comentarios

Para barallar unha lista, pode ser de utilidade a función `Enum.shuffle/1`.