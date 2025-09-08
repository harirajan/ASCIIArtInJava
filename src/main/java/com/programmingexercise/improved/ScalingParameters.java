package com.programmingexercise.improved;

/**
 * Immutable class holding scaling parameters for coordinate transformation.
 * Improvements over the original implementation:
 * - Immutable design for thread safety
 * - Input validation
 * - Clear documentation
 */
public class ScalingParameters {
    private final double scaleFactor;
    private final double precisionFactor;
    
    public ScalingParameters(double scaleFactor, double precisionFactor) {
        if (scaleFactor <= 0) {
            throw new IllegalArgumentException("Scale factor must be positive, got: " + scaleFactor);
        }
        if (precisionFactor <= 0) {
            throw new IllegalArgumentException("Precision factor must be positive, got: " + precisionFactor);
        }
        
        this.scaleFactor = scaleFactor;
        this.precisionFactor = precisionFactor;
    }
    
    public double getScaleFactor() {
        return scaleFactor;
    }
    
    public double getPrecisionFactor() {
        return precisionFactor;
    }
    
    @Override
    public String toString() {
        return String.format("ScalingParameters{scale=%.2f, precision=%.2f}", 
                           scaleFactor, precisionFactor);
    }
}
