# Sexto exercicio: almacenando pares chave-valor

Para esta práctica debes crear un módulo `db` que funcione como un
almacenamento chave-valor baseado en listas. A interface pública do
módulo é:

+ `db:new() -> db_ref` Crea o almacén de datos.

+ `db:write(db_ref, key, element) -> new_db_ref` Inserta un novo
elemento no almacén `db_ref`.

+ `db:delete(db_ref, key) -> new_db_ref` Elimina a primeira ocorrencia
da chave `key` no almacén `db_ref`.

+ `db:read(db_ref, key) -> {:ok, element} | {:error, :not_found}`
Recupera a primeira ocorrencia da chave `key` no almacén `db_ref`, ou
un valor de erro se non existe.

+ `db:match(db_ref, element) -> [key, ..., keyN]` Recupera todas as
chaves que conteñan o valor `Element`.

+ `db:destroy(db_ref) -> :ok` Elimina o almacenamento `db_ref`.

## Requisitos non funcionais

Na implementación deste módulo non podes usar as librerías `List`(nin
`:lists`), nin `Enum`.

## Notas

Non é preciso empregar un tipo de dato opaco para implementar as
referencias ao almacén de datos `db_ref`: pódese empregar a propia
lista.