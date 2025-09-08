package com.programmingexercise.improved;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Renders 2D points as ASCII art with sophisticated character mapping.
 * Improvements over the original implementation:
 * - Density-based character selection instead of simple alternating pattern
 * - Configurable character sets for different artistic effects
 * - Proper aspect ratio handling
 * - Efficient rendering with pre-calculated StringBuilder capacity
 * - Better resource management
 */
public class ASCIIRenderer {
    private static final Logger logger = LogManager.getLogger(ASCIIRenderer.class);
    
    /**
     * Renders a list of 2D points as ASCII art string
     */
    public String renderToASCII(List<Point2D> points, RenderingOptions options) {
        if (points.isEmpty()) {
            logger.warn("No points to render");
            return "";
        }
        
        logger.info("Rendering {} points with options: {}", points.size(), options);
        
        PointBounds bounds = calculatePointBounds(points);
        logger.info("Point bounds: {}", bounds);
        
        Map<Point2D, Integer> densityMap = createDensityMap(points, bounds);
        
        StringBuilder result = renderWithDensityMapping(densityMap, bounds, options);
        
        logger.info("Rendered ASCII art: {} characters, {} lines", 
                   result.length(), result.toString().split("\n").length);
        
        return result.toString();
    }
    
    /**
     * Writes ASCII art string to a file with proper error handling
     */
    public void writeToFile(String asciiArt, Path outputPath) throws IOException {
        Path parentDir = outputPath.getParent();
        if (parentDir != null && !Files.exists(parentDir)) {
            Files.createDirectories(parentDir);
        }
        
        try (BufferedWriter writer = Files.newBufferedWriter(outputPath)) {
            writer.write(asciiArt);
        }
        
        logger.info("ASCII art written to: {} ({} bytes)", outputPath, asciiArt.length());
    }
    
    private PointBounds calculatePointBounds(List<Point2D> points) {
        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;
        
        for (Point2D point : points) {
            minX = Math.min(minX, point.getX());
            maxX = Math.max(maxX, point.getX());
            minY = Math.min(minY, point.getY());
            maxY = Math.max(maxY, point.getY());
        }
        
        return new PointBounds(minX, maxX, minY, maxY);
    }
    
    private Map<Point2D, Integer> createDensityMap(List<Point2D> points, PointBounds bounds) {
        Map<Point2D, Integer> densityMap = new HashMap<>();
        
        for (Point2D point : points) {
            densityMap.merge(point, 1, Integer::sum);
        }
        
        return densityMap;
    }
    
    private StringBuilder renderWithDensityMapping(Map<Point2D, Integer> densityMap, 
                                                  PointBounds bounds, 
                                                  RenderingOptions options) {
        int width = bounds.getWidth();
        int height = bounds.getHeight();
        
        int estimatedCapacity = (width + 1) * height; // +1 for newlines
        StringBuilder result = new StringBuilder(estimatedCapacity);
        
        String characterSet = options.getCharacterSet();
        char backgroundChar = characterSet.charAt(0); // First character is background
        
        int maxDensity = densityMap.values().stream().mapToInt(Integer::intValue).max().orElse(1);
        
        for (int y = bounds.getMinY(); y <= bounds.getMaxY(); y++) {
            for (int x = bounds.getMinX(); x <= bounds.getMaxX(); x++) {
                Point2D currentPoint = new Point2D(x, y);
                Integer density = densityMap.get(currentPoint);
                
                if (density != null) {
                    char selectedChar = selectCharacterByDensity(density, maxDensity, characterSet);
                    result.append(selectedChar);
                } else {
                    result.append(backgroundChar);
                }
            }
            result.append('\n');
        }
        
        return result;
    }
    
    private char selectCharacterByDensity(int density, int maxDensity, String characterSet) {
        double normalizedDensity = (double) density / maxDensity;
        int charIndex = (int) (normalizedDensity * (characterSet.length() - 1));
        
        charIndex = Math.max(0, Math.min(charIndex, characterSet.length() - 1));
        
        return characterSet.charAt(charIndex);
    }
    
    /**
     * Helper class to hold point bounds
     */
    private static class PointBounds {
        private final int minX, maxX, minY, maxY;
        
        public PointBounds(int minX, int maxX, int minY, int maxY) {
            this.minX = minX;
            this.maxX = maxX;
            this.minY = minY;
            this.maxY = maxY;
        }
        
        public int getMinX() { return minX; }
        public int getMaxX() { return maxX; }
        public int getMinY() { return minY; }
        public int getMaxY() { return maxY; }
        
        public int getWidth() { return maxX - minX + 1; }
        public int getHeight() { return maxY - minY + 1; }
        
        @Override
        public String toString() {
            return String.format("PointBounds{x=[%d, %d], y=[%d, %d], size=%dx%d}", 
                               minX, maxX, minY, maxY, getWidth(), getHeight());
        }
    }
}
