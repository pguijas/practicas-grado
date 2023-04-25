# Cuarto exercicio: Manipulando listas

Para realizar esta práctica, debes crear un módulo `manipulating` coas
seguintes funcións públicas:

+ `filter/2` Dadas unha lista de enteiros `L` e un enteiro `N`,
devolve unha lista que contén os elementos de `L` que son máis
pequenos ou iguais ca `N`. Por exemplo:

    ```filter([1,2,3,4,5],3) -> [1,2,3]```

+ `reverse/1` Dada unha lista en elementos, devolve unha lista que
contén os mesmos elementos pero en orde inversa. Por exemplo:

    ```reverse([1,"two",3]) -> [3,"two",1]```

+ `concatenate/1` Dada unha lista de listas `L` (lista anidada),
devolve unha lista que é o resultado de concaternar os elementos de
`L`. Por exemplo:

    ```concatenate([[1,"two",3], [], [4,:five]]) -> [1,"two",3,4,:five]```

+ `flatten/1` Dada unha lista de listas anidadas de elementos `L`
(lista de listas de listas... de calquera profundidade), devolve unha
lista que é o resultado de _aplanar_ `L`. Por exemplo:

    ```flatten([[1,["two",[3],[]]], [[[4]]], [:five,6]]) -> [1,"two",3,4,:five,6]```

## Requisitos non funcionais

O obxecto da tarefa é a manipulación de listas, polo que non podes
usar as funcións do módulo `List` (nin as do seu equivalente Erlang
`:lists`), nin as do módulo `Enum`.

Para efectos desta práctica, tamén imos considerar o operador `++`
como unha función sobre listas que tampouco debes empregar.
