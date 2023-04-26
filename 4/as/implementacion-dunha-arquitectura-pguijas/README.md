# Arquitectura do Software
## Segunda práctica (curso 2021/2022)
### Implementación dunha arquitectura: P2pedro

`P2pedro` é un exemplo básico dunha rede p2p con forma de anel.
O porpósito inicial e final deste módulo é "curiosear" coa arquitectura p2p, polo cal o seu funcionamiento deixarase nun 2º plano para poder centrarnos en como o fai a arquitectura.

Destacar que xestionar unha rede que pretende actualizar os estados de todos os nodos nun intervalo curto de tempo non é unha tarefa doada como en solucións centralizadas poden facelo de xeito máis eficiente, pero o que buscamos e non ter ese único punto orquestador e que todos os nodos, falando entre eles consigan facelo de xeito descentralizado.

Deste modo, o obxetivo da nosa rede é conseguir un novo estado común a todos os nodos en cada iteración (unha iteración producirase cuando llo indiquemos a calquer nodo `start_merge/1`).
Dito estado común consistirá nunha media aritmética dos distintos estados internos dos modelos (que obviamente poden cambiarse cando o usuario o desexe `set_status/2`)

O funcionamiento é moi doado, no momento no que se da a orden dun merge (`start_merge/1`), orixinaranse 2 recorridos no círculo (previos e seguintes).
Deste modo, cando se atopen ditos recorridos mandaranse os resultados parciales ao nodo iniciador.
Este nodo fusionará os resultados obtendo un novo estado común e difundindoo do mesmo xeito que se consultaron os estados dos nodos.

Como traballo futuro, sería interesante profundizar nun tipo de rede máis complexa, dado que si nos paramos a pensar, na arquitectura p2p en anel, calquer peer é un punto de falla e cualquier nodo malicioso podería facernos un DOS.
Relacionado con esto, non controlamos as caidas abruptas e 2 destas poden significar a caida da nosa rede, polo que en futuras versións sería interesante abordarlo.
Así mesmo, tamén podría ser interesante investigar máis cautelosamente os distintos tipos de colisiones e implementar mecanismos máis sofisticados para mitigalos (Actualmente no noso sistema, si un novo merge se atopa cun nodo pendiente dunha actualización previa, deterase ese novo merge.)
