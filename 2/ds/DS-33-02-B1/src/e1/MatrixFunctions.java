package e1;

// Returns the maximun value of a matrix
public class MatrixFunctions {

    public static int max(int[][] a) {
        int comparador = a[0][0];

        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[i].length; j++) {
                if (a[i][j] > comparador) {
                    comparador = a[i][j];
                }
            }
        }

        return comparador;
    }

// Returns the sum of the values of a given row
    public static int rowSum(int[][] a, int row) {
        int suma = 0;

        for (int i = 0; i < a[row].length; i++) {
            suma = suma + a[row][i];
        }

        return suma;
    }

// Returns the sum of the values of a given column
    public static int columnSum(int[][] a, int column) {
        int suma = 0;

        for (int i = 0; i < a.length; i++) {
            if (column < a[i].length) {
                suma = suma + a[i][column];
            }
        }

        return suma;
    }

// Sums the value of each row and returns the results in an array .
    public static int[] allRowSums(int[][] a) {
        int suma[] = new int[a.length];

        for (int i = 0; i < a.length; i++) {
            suma[i] = suma[i] + rowSum(a, i);

        }

        return suma;
    }

// Sums the value of each column and returns the results in an array .
// If a position does not exist because the array is " ragged " that position is considered a zero value .
    public static int[] allColumnSums(int[][] a) {
        int tamaño = 0;

        for (int i = 0; i < a.length; i++) {
            if (tamaño < a[i].length) {
                tamaño = a[i].length;
            }
        }

        int suma[] = new int[tamaño];
        for (int i = 0; i < tamaño; i++) {
            suma[i] = suma[i] + columnSum(a, i);
        }

        return suma;
    }

// Checks if an array is "row - magic ", that is , if all its rows have the same
// sum of all its values .
    public static boolean isRowMagic(int[][] a) {
        boolean filaMagica = true;
        int suma1 = rowSum(a, 0);

        for (int i = 1; i < a.length; i++) {
            if (suma1 != rowSum(a, i)) {
                filaMagica = false;
            }
        }

        return filaMagica;
    }

// Checks if an array is " column - magic ", that is , if all its columns have
// the same sum of all its values .
    public static boolean isColumnMagic(int[][] a) {
        boolean columnaMagica = true;
        int suma1 = columnSum(a, 0);

        for (int i = 1; i < a.length; i++) {
            if (suma1 != columnSum(a, i)) {
                columnaMagica = false;
            }
        }

        return columnaMagica;
    }

// Checks that a matrix is square , that is , it has the same number of rows
// as columns and all rows have the same length . 
    public static boolean isSquare(int[][] a) {
        boolean matrizCuadrada = false;
        int tamaño = 0;

        for (int i = 0; i < a.length; i++) {
            if (tamaño < a[i].length) {
                tamaño = a[i].length;
            }
        }

        int contadorFilas[] = new int[a.length];
        int contadorColumnas[] = new int[tamaño];

        if (contadorFilas.length == contadorColumnas.length) {
            matrizCuadrada = true;
        }

        return matrizCuadrada;
    }

// Check if the matrix is a magic square . A matrix is magic square if it is
// square , all the rows add up to the same , all the columns add up to the
// same and the two main diagonals add up to the same . Also all these sums
// are the same .
    public static boolean isMagic(int[][] a) {
        boolean esMagica = false;
        int diagonalPrincipal = 0;
        int diagonalSecundaria = 0;
        int sumaColumnas = 0;
        int sumaFilas = 1;

        if (isSquare(a)) {
            if (isColumnMagic(a) && isRowMagic(a)) {
                sumaColumnas = columnSum(a, 0);
                sumaFilas = rowSum(a, 0);
            }
            for (int i = 0; i < a.length; i++) {
                for (int j = 0; j < a[i].length; j++) {
                    if (i == j) {
                        diagonalPrincipal = diagonalPrincipal + a[i][j];
                    }

                    if (i + j == a.length - 1) {
                        diagonalSecundaria = diagonalSecundaria + a[i][j];
                    }
                }
            }
            if (sumaColumnas == sumaFilas && diagonalPrincipal == diagonalSecundaria) {
                if (sumaColumnas == diagonalPrincipal) {
                    esMagica = true;
                }
            }
        }

        return esMagica;
    }

// Checks if the given matrix forms a sequence , that is , it is square
// (of order n) and contains all the digits from 1 to n * n, regardless of
// their order .
    public static boolean isSequence(int[][] a) {
        boolean isSequence = false;
        int comparador = 1;
        int tamañoMaximo = a.length * a[0].length;

        if (isSquare(a) == true) {
            for (int i = 0; i < a.length; i++) {
                for (int j = 0; j < a[i].length; j++) {
                    if (comparador <= tamañoMaximo) {
                        if (a[i][j] == comparador) {
                            comparador = comparador + 1;
                            i = 0;
                            j = -1;
                        }
                    }
                }
            }
        }

        if (comparador - 1 == tamañoMaximo) {
            isSequence = true;
        }

        return isSequence;
    }

}
