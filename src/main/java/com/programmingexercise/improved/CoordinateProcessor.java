package com.programmingexercise.improved;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Processes geographic coordinates and transforms them to 2D points for ASCII rendering.
 * Improved from the original implementation with:
 * - Efficient Set-based storage instead of HashMap with Lists
 * - Proper resource management with try-with-resources
 * - Stream-based processing for better performance
 * - Input validation and error handling
 * - Single-pass coordinate bounds calculation
 */
public class CoordinateProcessor {
    private static final Logger logger = LogManager.getLogger(CoordinateProcessor.class);
    
    /**
     * Reads coordinates from a CSV file with proper error handling and validation.
     * Expected CSV format: any columns, with latitude in column 2 and longitude in column 3 (0-indexed)
     */
    public List<Coordinate> readCoordinates(Path filePath) throws IOException {
        if (!Files.exists(filePath)) {
            throw new IOException("Input file does not exist: " + filePath);
        }
        
        if (!Files.isReadable(filePath)) {
            throw new IOException("Input file is not readable: " + filePath);
        }
        
        List<Coordinate> coordinates = new ArrayList<>();
        
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String firstLine = reader.readLine();
            if (firstLine == null) {
                throw new IOException("Input file is empty");
            }
            
            boolean hasHeader = isHeaderLine(firstLine);
            if (!hasHeader) {
                Coordinate coord = parseCoordinateLine(firstLine, 1);
                if (coord != null) {
                    coordinates.add(coord);
                }
            }
            
            String line;
            int lineNumber = hasHeader ? 2 : 2;
            while ((line = reader.readLine()) != null) {
                Coordinate coord = parseCoordinateLine(line, lineNumber);
                if (coord != null) {
                    coordinates.add(coord);
                }
                lineNumber++;
            }
        }
        
        if (coordinates.isEmpty()) {
            throw new IOException("No valid coordinates found in file");
        }
        
        logger.info("Successfully read {} coordinates from {}", coordinates.size(), filePath);
        return coordinates;
    }
    
    private boolean isHeaderLine(String line) {
        String[] parts = line.split(",");
        if (parts.length < 4) {
            return false;
        }
        
        try {
            Double.parseDouble(parts[2].trim());
            Double.parseDouble(parts[3].trim());
            return false; // Successfully parsed as numbers, not a header
        } catch (NumberFormatException e) {
            return true; // Failed to parse as numbers, likely a header
        }
    }
    
    private Coordinate parseCoordinateLine(String line, int lineNumber) {
        if (line.trim().isEmpty()) {
            return null;
        }
        
        String[] parts = line.split(",");
        if (parts.length < 4) {
            logger.warn("Line {}: Expected at least 4 columns, got {}", lineNumber, parts.length);
            return null;
        }
        
        try {
            double latitude = Double.parseDouble(parts[2].trim());
            double longitude = Double.parseDouble(parts[3].trim());
            return new Coordinate(latitude, longitude);
        } catch (NumberFormatException e) {
            logger.warn("Line {}: Invalid coordinate format: {}", lineNumber, e.getMessage());
            return null;
        } catch (IllegalArgumentException e) {
            logger.warn("Line {}: Invalid coordinate values: {}", lineNumber, e.getMessage());
            return null;
        }
    }
    
    /**
     * Processes coordinates and transforms them to 2D points using efficient algorithms.
     * Improvements over original:
     * - Uses Set instead of HashMap to automatically handle duplicates
     * - Single-pass bounds calculation
     * - Proper coordinate transformation with validation
     */
    public List<Point2D> processCoordinates(List<Coordinate> coordinates, ScalingParameters params) {
        if (coordinates.isEmpty()) {
            throw new IllegalArgumentException("Coordinate list cannot be empty");
        }
        
        logger.info("Processing {} coordinates with scaling parameters: {}", 
                   coordinates.size(), params);
        
        CoordinateBounds bounds = calculateBounds(coordinates);
        logger.info("Coordinate bounds: {}", bounds);
        
        Set<Point2D> uniquePoints = coordinates.stream()
            .map(coord -> transformCoordinate(coord, params, bounds))
            .collect(Collectors.toSet());
        
        List<Point2D> points = new ArrayList<>(uniquePoints);
        logger.info("Transformed to {} unique 2D points", points.size());
        
        return points;
    }
    
    private CoordinateBounds calculateBounds(List<Coordinate> coordinates) {
        double minLat = Double.MAX_VALUE;
        double maxLat = Double.MIN_VALUE;
        double minLon = Double.MAX_VALUE;
        double maxLon = Double.MIN_VALUE;
        
        for (Coordinate coord : coordinates) {
            minLat = Math.min(minLat, coord.getLatitude());
            maxLat = Math.max(maxLat, coord.getLatitude());
            minLon = Math.min(minLon, coord.getLongitude());
            maxLon = Math.max(maxLon, coord.getLongitude());
        }
        
        return new CoordinateBounds(minLat, maxLat, minLon, maxLon);
    }
    
    private Point2D transformCoordinate(Coordinate coord, ScalingParameters params, CoordinateBounds bounds) {
        double normalizedLat = (coord.getLatitude() - bounds.getMinLat()) / 
                              (bounds.getMaxLat() - bounds.getMinLat());
        double normalizedLon = (coord.getLongitude() - bounds.getMinLon()) / 
                              (bounds.getMaxLon() - bounds.getMinLon());
        
        double scaledLat = normalizedLat * params.getScaleFactor();
        double scaledLon = normalizedLon * params.getScaleFactor();
        
        int x = (int) Math.round(scaledLon * params.getPrecisionFactor());
        int y = (int) Math.round(scaledLat * params.getPrecisionFactor());
        
        return new Point2D(x, y);
    }
    
    /**
     * Helper class to hold coordinate bounds
     */
    private static class CoordinateBounds {
        private final double minLat, maxLat, minLon, maxLon;
        
        public CoordinateBounds(double minLat, double maxLat, double minLon, double maxLon) {
            this.minLat = minLat;
            this.maxLat = maxLat;
            this.minLon = minLon;
            this.maxLon = maxLon;
        }
        
        public double getMinLat() { return minLat; }
        public double getMaxLat() { return maxLat; }
        public double getMinLon() { return minLon; }
        public double getMaxLon() { return maxLon; }
        
        @Override
        public String toString() {
            return String.format("Bounds{lat=[%.6f, %.6f], lon=[%.6f, %.6f]}", 
                               minLat, maxLat, minLon, maxLon);
        }
    }
}
