# Quinto exercicio: implementando algoritmos de ordenación

Para esta práctica debes crear un módulo `sorting` con dúas funcións
públicas de ordenación de listas. As dúas funcións reciben como
argumento unha lista e ordénana da seguinte maneira:

+ `quicksort/1` Implementa o método
[quicksort](http://en.wikipedia.org/wiki/Quicksort). A cabeza da lista
que se recibe como argumento serve como pivote.

+ `mergesort/1` Implementa o método
[mergesort](https://en.wikipedia.org/wiki/Merge_sort). Divide a lista
en dúas de lonxitude semellante. Ordena recursivamente cada unha das
listas e despois mestura os seus elementos.

## Requisitos non funcionais

Na implementación de `mergesort` podes empregar a función `Enum.split/2`.

## Notas

O algoritmo _quicksort_ aplica os seguintes pasos:

1. Escoller un pivote e dividir a lista en dúas: unha cos elementos
menores que o pivote e outra cos maiores.
2. Ordenar recursivamente as dúas listas.
3. Xuntar as dúas listas ordenadas co pivote no medio.