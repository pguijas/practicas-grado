# Décimo exercicio: paralelizando tarefas

Para completar esta tarefa de programación debes crear un módulo `measure` cunha función pública `run/2`.

```run(lista_de_funcions, numero_de_elementos)```

Esta función debe crear, nun primeiro momento, `N` tarefas (`Task`), onde `N` se corresponde co número de funcións indicado como primeiro argumento (isto é, `length(lista_de_funcions)`). Estas tarefas crearán cadansúa lista de `numero_de_elementos`, en paralelo.

Unha vez que a creación de datos remate, crearánse outras `N` tarefas, que executarán as funcións da `lista_de_funcions` empregando os datos xerados no paso previo, en paralelo, pero nun máximo de 10 segundos. 

Así, por exemplo:

```Measure.run([{Manipulating, :reverse}, {Manipulating, :flatten}, {Sorting, :quicksort}, {Sorting, :mergesort}], 10000)```

Creará 4 tarefas que construirán as 4 listas, de 10000 elementos cada unha, necesarias para executar as 4 funcións indicadas. Agardarase o tempo que sexa necesario polas tarefas de creación. Rematadas todas as tarefas de creación, crearanse outras 4 tarefas, que executarán as 4 funcións indicadas empregando como argumentos as listas construídas polas tarefas anteriores. Agardarase un máximo de 10 segundos polo remate das tarefas de execución. Pasados eses 10 segundos, imprimirase unha saída como a seguinte:

      -------------------------------------------
     | Creación de datos     : 0.260628       sec |
     | Manipulating:reverse  : 0.005013       sec |
      --------------------------------------------

No caso de que algunha tarefa non rematara no tempo estipulado, a saída será como a seguinte:

      --------------------------------------------
     | Creación de datos     : 19.884944      sec |
     | Sorting:quicksort     : interrompida       |
     | Sorting:mergesort     : 6.646388       sec |
      --------------------------------------------

## Notas

As funcións que debe soportar a función `run/2` son: `Manipulating.reverse/1`, `Manipulating.flatten/1`, `Sorting.quicksort/1` e `Sorting.mergesort/1`. Nótese que todas estas funcións reciben listas de enteiros, agás `Manipulating.flatten/1`, que recibe unha lista de listas. Isto debe terse en conta nas tarefas de creación de datos.

Para a creación das listas de datos, resultará útil a sintaxe `1..n` de Elixir. *NON* debe usarse a función `Create.create/1` do exercicio 2, xa que as restricións impostas no enunciado fan moi probable que a súa implementación non sexa eficiente.

As listas creadas deben conter enteiros aleatorios de valores entre 0 e 1000. Para xerar estes enteiros resultará útil a función `:rand.uniform/1`.

Para a medición de tempos, resultarán útiles as funcións `:erlang.timestamp()` e `:timer.now_diff/2`.