package com.programmingexercise.improved;

/**
 * Immutable coordinate class representing latitude and longitude.
 * Improvements over the original implementation:
 * - Immutable design for thread safety
 * - Input validation
 * - Proper equals/hashCode implementation
 * - Clear documentation
 */
public class Coordinate {
    private final double latitude;
    private final double longitude;
    
    public Coordinate(double latitude, double longitude) {
        if (latitude < -90.0 || latitude > 90.0) {
            throw new IllegalArgumentException("Latitude must be between -90 and 90 degrees, got: " + latitude);
        }
        if (longitude < -180.0 || longitude > 180.0) {
            throw new IllegalArgumentException("Longitude must be between -180 and 180 degrees, got: " + longitude);
        }
        
        this.latitude = latitude;
        this.longitude = longitude;
    }
    
    public double getLatitude() {
        return latitude;
    }
    
    public double getLongitude() {
        return longitude;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Coordinate that = (Coordinate) obj;
        return Double.compare(that.latitude, latitude) == 0 &&
               Double.compare(that.longitude, longitude) == 0;
    }
    
    @Override
    public int hashCode() {
        long latBits = Double.doubleToLongBits(latitude);
        long lonBits = Double.doubleToLongBits(longitude);
        return (int) (latBits ^ (latBits >>> 32) ^ lonBits ^ (lonBits >>> 32));
    }
    
    @Override
    public String toString() {
        return String.format("Coordinate{lat=%.6f, lon=%.6f}", latitude, longitude);
    }
}
