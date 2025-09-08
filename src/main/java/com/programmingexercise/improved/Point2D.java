package com.programmingexercise.improved;

/**
 * Immutable 2D point class for ASCII rendering coordinates.
 * Improvements over the original implementation:
 * - Immutable design for thread safety
 * - Proper equals/hashCode for Set operations
 * - Clear documentation
 */
public class Point2D {
    private final int x;
    private final int y;
    
    public Point2D(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Point2D point2D = (Point2D) obj;
        return x == point2D.x && y == point2D.y;
    }
    
    @Override
    public int hashCode() {
        return 31 * x + y;
    }
    
    @Override
    public String toString() {
        return String.format("Point2D{x=%d, y=%d}", x, y);
    }
}
