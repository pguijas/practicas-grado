package e3;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class RectangleTest {
    Rectangle r12, r22, r23, r24, r33, r42;
    double delta = 0.000001;

    
    @Before
    public void setUp() {
        r12 = new Rectangle(1, 2);
        r22 = new Rectangle(2, 2);
        r23 = new Rectangle(2, 3);
        r24 = new Rectangle(2, 4);
        r33 = new Rectangle(3, 3);
        r42 = new Rectangle(4, 2);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testSetNegativeBase() {
        Rectangle r1 = new Rectangle(-1, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetNegativeHeight() {
        Rectangle r1 = new Rectangle(1, -1);
    }

    @Test
    public void testCopyConstructor() {
        Rectangle r = new Rectangle(r12);
        assertEquals(1, r.getBase());
        assertEquals(2, r.getHeight());
    }

    @Test
    public void testSetBaseAndHeight() {
        assertEquals(1, r12.getBase());
        assertEquals(2, r12.getHeight());
    }

    @Test
    public void testIsSquare() {
        assertFalse(r12.isSquare());
        assertTrue(r22.isSquare());
        assertFalse(r23.isSquare());
        assertFalse(r24.isSquare());
        assertTrue(r33.isSquare());
        assertFalse(r42.isSquare());
    }

    @Test
    public void testArea() {
        assertEquals(2, r12.area());
        assertEquals(4, r22.area());
        assertEquals(6, r23.area());
        assertEquals(8, r24.area());
        assertEquals(9, r33.area());
        assertEquals(8, r42.area());
    }

    @Test
    public void testPerimeter() {
        assertEquals(6, r12.perimeter());
        assertEquals(8, r22.perimeter());
        assertEquals(10, r23.perimeter());
        assertEquals(12, r24.perimeter());
        assertEquals(12, r33.perimeter());
        assertEquals(12, r42.perimeter());
    }

    @Test
    public void testDiagonal() {
        assertEquals(2.23606797749979, r12.diagonal(), delta);
        assertEquals(2.82842712474619, r22.diagonal(), delta);
        assertEquals(3.60555127546398, r23.diagonal(), delta);
        assertEquals(4.47213595499958, r24.diagonal(), delta);
        assertEquals(4.24264068711928, r33.diagonal(), delta);
        assertEquals(4.47213595499958, r42.diagonal(), delta);
    }

    @Test
    public void testTurn() {
        r12.turn();
        assertEquals(2, r12.getBase());
        assertEquals(1, r12.getHeight());
        r22.turn();
        assertEquals(2, r22.getBase());
        assertEquals(2, r22.getHeight());
        r42.turn();
        assertEquals(2, r42.getBase());
        assertEquals(4, r42.getHeight());
    }

    @Test
    public void testPutHorizontal() {
        r12.putHorizontal();
        assertEquals(2, r12.getBase());
        assertEquals(1, r12.getHeight());
        r22.putHorizontal();
        assertEquals(2, r22.getBase());
        assertEquals(2, r22.getHeight());
        r42.putHorizontal();
        assertEquals(4, r42.getBase());
        assertEquals(2, r42.getHeight());
    }

    @Test
    public void testPutVertical() {
        r12.putVertical();
        assertEquals(1, r12.getBase());
        assertEquals(2, r12.getHeight());
        r22.putVertical();
        assertEquals(2, r22.getBase());
        assertEquals(2, r22.getHeight());
        r42.putVertical();
        assertEquals(2, r42.getBase());
        assertEquals(4, r42.getHeight());
    }

    @Test
    public void testEquals() {
        assertTrue(r12.equals(r12));
        assertFalse(r12.equals(null));
        assertFalse(r12.equals("Hola Mundo"));
        assertFalse(r22.equals(r23));
        assertTrue(r24.equals(r42));       
    }

    @Test
    public void testHashCode() {
        // equals rectangles return the same hash code
        assertTrue(r12.hashCode() == r12.hashCode());
        r12.setBase(5);
        assertTrue(r12.hashCode() == r12.hashCode());
        assertTrue(r24.hashCode() == r42.hashCode());
    }
    
}
