package e1;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals; 

public class MatrixFunctionsTest {

    // example arrays for testing
    private int[][] basic, allneg, nonsquare, negatives, rowmagic, colmagic,
            magic, durero, sagradaFamilia, notmagic1, notmagic2, notmagic3, 
            latin, notlatin, disorderedSequence;

    /**
     * Sets up the test fixture with some arrays to test. This method is called
     * before every test case method.
     */
    @Before
    public void setUp() {
        basic = new int[][] {{1, 2, 3}, 
                             {4, 5, 6}, 
                             {7, 8, 9}};
        
        allneg = new int[][]{{-10, -12, -3}, 
                             { -4,  -5, -6, -8}, 
                             { -7,  -8}}; //all neg and ragged
        
        nonsquare = new int[][]{{1, 2, 3}, 
                                {4, 5}, 
                                {6, 7, 8, 9}};
        
        negatives = new int[][]{{ 1, -2,  3}, 
                                { 4,  5,  6}, 
                                {-7,  8, -9}};
        
        rowmagic = new int[][]{{ 1, 2, 3}, 
                               {-1, 5, 2}, 
                               { 4, 0, 2}};
        
        colmagic = new int[][]{{1, -1, 4, 10}, 
                               {3,  5, 0, -6}};
        
        magic = new int[][]{{2, 2, 2}, 
                            {2, 2, 2}, 
                            {2, 2, 2}};
        
        durero = new int[][] {{16,  3,  2, 13},
                              { 5, 10, 11,  8},
                              { 9,  6,  7, 12},
                              { 4, 15, 14,  1}};

        sagradaFamilia = new int[][] {{ 1, 14, 14,  4},
                                      {11,  7,  6,  9},
                                      { 8, 10, 10,  5},
                                      {13,  2,  3, 15}};        

        notmagic1 = new int[][]{{1, 2, 3}, 
                                {4, 5, 6}, 
                                {6, 8, 9}}; //diag sums are not equal
        
        notmagic2 = new int[][]{{1, 5, 3}, 
                                {4, 5, 6}, 
                                {7, 8, 9}}; //diag sums are equal but rows are not
        
        notmagic3 = new int[][]{{2, 1, 2}, 
                                {1, 3, 1}, 
                                {2, 1, 2}}; //row-magic, col-magic, diag-magic but not magic
               
        latin = new int[][]{{1, 2, 3}, 
                            {2, 3, 1}, 
                            {3, 1, 2}};
        
        notlatin = new int[][]{{2, 1, 3}, 
                               {2, 3, 1}, 
                               {3, 1, 2}};        
        
        disorderedSequence = new int[][]{{ 2, 16, 4,  5}, 
                                         {15,  3, 1,  6}, 
                                         {10, 11, 7, 12},
                                         {14,  9, 8, 13}};    
    }

    // Test max is found correctly (last element in the search)
    @Test
    public void testMaxNormal() {
        assertEquals(9, MatrixFunctions.max(basic));
    }

    // Test max correct when all vals are negative
    @Test
    public void testMaxAllNeg() {
        assertEquals(-3, MatrixFunctions.max(allneg));
    }

    // Test row sum calculated correctly including for nonsquare arrays
    @Test
    public void testRowSum() {
        assertEquals(6, MatrixFunctions.rowSum(basic, 0));
        assertEquals(15, MatrixFunctions.rowSum(basic, 1));
        assertEquals(24, MatrixFunctions.rowSum(basic, 2));
        assertEquals(30, MatrixFunctions.rowSum(nonsquare, 2));
    }

    // Test column sum calculated correctly for standard cases
    @Test
    public void testColumnSum() {
        assertEquals(12, MatrixFunctions.columnSum(basic, 0));
        assertEquals(15, MatrixFunctions.columnSum(basic, 1));
        assertEquals(18, MatrixFunctions.columnSum(basic, 2));
    }

    // Test column sum calculated correctly for nonsquare arrays This checks for
    // sum of incomplete columns (from ragged arrays)
    @Test
    public void testColumnSumRagged() {
        assertEquals(11, MatrixFunctions.columnSum(nonsquare, 2));
        assertEquals(9, MatrixFunctions.columnSum(nonsquare, 3));
    }

    // Checks array of row sums correctly calculated
    @Test
    public void testAllRowSums() {
        int[] expected = new int[]{6, 15, 24};
        int[] actual = MatrixFunctions.allRowSums(basic);
        assertArrayEquals(expected, actual);
    }
    
    // Checks array of column sums correctly calculated
    @Test
    public void testAllColumnsSums() {
        int[] expected = new int[]{12, 15, 18};
        int[] actual = MatrixFunctions.allColumnSums(basic);
        assertArrayEquals(expected, actual);
    }    
    
    // Checks array of column sums correctly calculated for ragged array
    @Test
    public void testAllColumnsSumsRagged() {
        int[] expected = new int[]{11, 14, 11, 9};
        int[] actual = MatrixFunctions.allColumnSums(nonsquare);
        assertArrayEquals(expected, actual);
    }       
    
    // Test for row magic with a valid magic square
    @Test
    public void testIsRowMagicTrue() {
        assertEquals(true, MatrixFunctions.isRowMagic(rowmagic));
    }

    // Test for row magic where row sums are not the same
    @Test
    public void testIsRowMagicFalse() {
        assertEquals(false, MatrixFunctions.isRowMagic(basic));

    }
    
    // Test col magic where col sums are the same
    @Test
    public void testIsColumnMagicTrue() {
        assertEquals(true, MatrixFunctions.isColumnMagic(colmagic));
    }

    // Test col magic where col sums are not the same
    @Test
    public void testIsColumnMagicFalse() {
        assertEquals(false, MatrixFunctions.isColumnMagic(rowmagic));
    }
    
    // Test for square arrays
    @Test
    public void testIsSquareTrue() {
        assertEquals(true, MatrixFunctions.isSquare(basic));
    }

    // Test for non-square arrays
    @Test
    public void testIsSquareFalse() {
        assertEquals(false, MatrixFunctions.isSquare(nonsquare));
    }
    
    // Test where all conditions for magic square are met
    @Test
    public void testIsMagicTrue() {
        assertEquals(true, MatrixFunctions.isMagic(magic));
        assertEquals(true, MatrixFunctions.isMagic(durero));
        assertEquals(true, MatrixFunctions.isMagic(sagradaFamilia));
    }

    // Test magic square false because row and col sums are not the same
    @Test
    public void testIsMagicNotSquare() {
        assertEquals(false, MatrixFunctions.isMagic(allneg));
    }

    // Test magic square false because row and col sums are the same BUT diags are not
    @Test
    public void testIsMagicFalseBadDiags() {
        assertEquals(false, MatrixFunctions.isMagic(notmagic1));
    }

    // Test magic square false because (only) row sums are not the same
    @Test
    public void testIsMagicFalseBadRows() {
        assertEquals(false, MatrixFunctions.isMagic(notmagic2));
    }

    // Test magic square false because sums are not the same
    @Test
    public void testIsMagicFalseBadSum() {
        assertEquals(false, MatrixFunctions.isMagic(notmagic3));
    }
    
    // Test isSequence for a valid 3x3 array containing 1..9
    @Test
    public void testIsSequenceTrue() {
        assertEquals(true, MatrixFunctions.isSequence(basic));
        assertEquals(true, MatrixFunctions.isSequence(disorderedSequence));
    }

    // Test isSequence false because array is not square
    @Test
    public void testIsSequenceNotSquare() {
        assertEquals(false, MatrixFunctions.isSequence(nonsquare));
    }

    // Test isSequence false because array contains duplicates
    @Test
    public void testIsSequenceRepeatsFalse() {
        assertEquals(false, MatrixFunctions.isSequence(magic));
    }

    // Test isSequence false because array vals do not form a sequence
    @Test
    public void testIsSequenceBadValsFalse() {
        assertEquals(false, MatrixFunctions.isSequence(negatives));
    }
}
