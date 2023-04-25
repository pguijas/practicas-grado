
#
# Ejemplo de una máquina de Turing que acepta cadenas que contienen
# una c seguida o precedida de una a y una b.
#

# Estados
0 1 2 3 4 5 6 7;

# Símbolos de entrada
a b c;

# Símbolos de la cinta
blanco a b c;

# Estado inicial
0;

# Estados finales
4 7;

# Transiciones.
0 1 blanco blanco derecha;
1 1 a a derecha;
1 1 b b derecha;
1 1 c c derecha;
1 2 c c derecha;
2 3 a a derecha;
3 4 b b derecha;
1 5 c c izquierda;
5 6 b b izquierda;
6 7 a a izquierda;

