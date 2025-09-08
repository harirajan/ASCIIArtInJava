package com.programmingexercise.improved;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * Main application class for generating ASCII art from geographic coordinates.
 * This improved version addresses the issues in the original implementation:
 * - Proper object-oriented design with separation of concerns
 * - Thread-safe implementation without static variables
 * - Better error handling and resource management
 * - Configurable and cross-platform compatible
 */
public class ASCIIArtGenerator {
    private static final Logger logger = LogManager.getLogger(ASCIIArtGenerator.class);
    
    private final ConfigurationManager configManager;
    private final CoordinateProcessor processor;
    private final ASCIIRenderer renderer;
    
    public ASCIIArtGenerator(ConfigurationManager configManager) {
        this.configManager = configManager;
        this.processor = new CoordinateProcessor();
        this.renderer = new ASCIIRenderer();
    }
    
    /**
     * Main entry point for the application
     */
    public static void main(String[] args) {
        try {
            Instant start = Instant.now();
            
            String configPath = args.length > 0 ? args[0] : "src/main/resources/improved-config.properties";
            ConfigurationManager config = new ConfigurationManager(configPath);
            
            ASCIIArtGenerator generator = new ASCIIArtGenerator(config);
            generator.generateASCIIArt();
            
            Instant end = Instant.now();
            logger.info("ASCII art generation completed in {} ms", 
                       Duration.between(start, end).toMillis());
            
        } catch (Exception e) {
            logger.error("Failed to generate ASCII art: {}", e.getMessage(), e);
            System.exit(1);
        }
    }
    
    /**
     * Generates ASCII art from the configured input file
     */
    public void generateASCIIArt() throws IOException {
        logger.info("Starting ASCII art generation");
        
        Path inputPath = configManager.getInputPath();
        logger.info("Reading coordinates from: {}", inputPath);
        List<Coordinate> coordinates = processor.readCoordinates(inputPath);
        logger.info("Successfully read {} coordinates", coordinates.size());
        
        ScalingParameters scalingParams = configManager.getScalingParameters();
        List<Point2D> points = processor.processCoordinates(coordinates, scalingParams);
        logger.info("Processed coordinates to {} unique points", points.size());
        
        RenderingOptions renderOptions = configManager.getRenderingOptions();
        String asciiArt = renderer.renderToASCII(points, renderOptions);
        logger.info("Generated ASCII art with {} characters", asciiArt.length());
        
        Path outputPath = configManager.getOutputPath();
        renderer.writeToFile(asciiArt, outputPath);
        logger.info("ASCII art written to: {}", outputPath);
    }
}
