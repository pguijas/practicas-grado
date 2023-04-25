
#
# MT que acepta a^n b^n c^n
#

# Estados
A B C D E H;

# Símbolos de entrada
a b c;

# Símbolos de la cinta
blanco a b c X Y Z;

# Estado inicial
A;

# Estados finales
H;

# Transiciones
A B a X derecha;
A E Y Y derecha;
A H blanco blanco derecha;
B B a a derecha;
B B Y Y derecha;
B C b Y derecha;
C C b b derecha;
C C Z Z derecha;
C D c Z izquierda;
D D a a izquierda;
D D b b izquierda;
D D Y Y izquierda;
D D Z Z izquierda;
D A X X derecha;
E E Y Y derecha;
E E Z Z derecha;
E H blanco blanco derecha;
