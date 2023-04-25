package e3;
        
// Represents a rectangle

import static java.lang.Math.sqrt;

public class Rectangle {
    int base;
    int height;
    
    // Initializes a new rectangle with the values passed by parameter .
    // Throws IllegalArgumentException if a a negative value is passed to any of
    // the dimensions .
    public Rectangle(int base, int height) {
        if (base <= 0 || height <= 0){
            throw new IllegalArgumentException("Introduciuse uns parametros negativos");
        }
        this.base = base;
        this.height = height;
    }
    
    // Copy constructor
    public Rectangle(Rectangle r) {
        this.base = r.base;
        this.height = r.height;
    }
    
    // Getters
    public int getBase() {
        return this.base;
    }

    public int getHeight() {
        return this.height;
    }
    
    // Setters . Throw IllegalArgumentException if the parameters are negative .
    public void setBase(int base) {
        if (base <= 0){
            throw new IllegalArgumentException("Introduciuse uns parametros negativos");
        }
        this.base = base;
    }

    public void setHeight(int height) {
        if (height <= 0){
            throw new IllegalArgumentException("Introduciuse uns parametros negativos");
        }
        this.height = height;
    }
  
    // Return true if the rectangle is a square
    public boolean isSquare() {
        boolean cuadrado = false;
        if (this.base == this.height){
            cuadrado = true;
        }
        return cuadrado;
    }
    
    // Calculate the area of the rectangle .
    public int area() {
        return (this.base * this.height);
    }
    
    // Calculate the perimeter of the rectangle .
    public int perimeter() {
        return this.base*2 + this.height*2;
    }
    
    // Calculate the length of the diagonal
    public double diagonal() {
        return sqrt(Math.pow(this.base, 2) + Math.pow(this.height, 2));
    }
    
    // Turn this rectangle 90 degrees ( changing base by height ).
    public void turn() {
        int cambio;
        cambio = this.base;
        this.base = this.height;
        this.height = cambio;
    }
    
    // Ensure that this rectangle is oriented horizontally ( the base is greater
    // or equal than the height ).
    public void putHorizontal() {
        if(this.base <= this.height ){
            turn();
        }
    }
    
    // Ensure that this rectangle is oriented vertically ( the height is greater
    // or equal than the base ).
    public void putVertical() {
        if(this.base >= this.height ){
            turn();
        }    
    }
    
    // Two rectangles are equal if the base and the height are the same .
    // It does not take into account if the rectangle is rotated .
    public boolean equals(Object obj) {
        boolean igual = false;
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Rectangle other = (Rectangle) obj;
        Rectangle r1 = new Rectangle(this);
        other.putHorizontal();
        r1.putHorizontal();
        if(other.base == r1.base && other.height == r1.height){
            igual = true;
        }
        return igual;
    }




    // It complies with the hashCode contract and is consistent with the equals .
    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + this.height + this.base;
        return hash;
    }
    
}
