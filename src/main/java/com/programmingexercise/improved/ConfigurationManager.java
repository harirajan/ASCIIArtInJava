package com.programmingexercise.improved;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Manages application configuration with proper validation and defaults.
 * Improvements over the original implementation:
 * - Cross-platform compatible paths
 * - Proper validation of all configuration parameters
 * - Sensible default values
 * - Support for both file and classpath resources
 */
public class ConfigurationManager {
    private static final Logger logger = LogManager.getLogger(ConfigurationManager.class);
    
    private final Properties properties;
    
    private static final String DEFAULT_INPUT_FILE = "sample-coordinates.csv";
    private static final String DEFAULT_OUTPUT_FILE = "improved-ascii-art.txt";
    private static final double DEFAULT_SCALE_FACTOR = 50.0;
    private static final double DEFAULT_PRECISION_FACTOR = 100.0;
    private static final String DEFAULT_CHARACTER_SET = " .:-=+*#%@";
    
    public ConfigurationManager(String configPath) throws IOException {
        this.properties = loadConfiguration(configPath);
        validateConfiguration();
        logger.info("Configuration loaded successfully from: {}", configPath);
    }
    
    private Properties loadConfiguration(String configPath) throws IOException {
        Properties props = new Properties();
        
        Path configFile = Paths.get(configPath);
        if (Files.exists(configFile)) {
            try (InputStream input = Files.newInputStream(configFile)) {
                props.load(input);
                logger.info("Loaded configuration from file: {}", configFile);
                return props;
            }
        }
        
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(configPath)) {
            if (input != null) {
                props.load(input);
                logger.info("Loaded configuration from classpath: {}", configPath);
                return props;
            }
        }
        
        logger.warn("Configuration file not found: {}. Using default values.", configPath);
        return props; // Return empty properties, will use defaults
    }
    
    private void validateConfiguration() {
        double scaleFactor = getScaleFactor();
        if (scaleFactor <= 0) {
            throw new IllegalArgumentException("Scale factor must be positive, got: " + scaleFactor);
        }
        
        double precisionFactor = getPrecisionFactor();
        if (precisionFactor <= 0) {
            throw new IllegalArgumentException("Precision factor must be positive, got: " + precisionFactor);
        }
        
        String characterSet = getCharacterSet();
        if (characterSet.isEmpty()) {
            throw new IllegalArgumentException("Character set cannot be empty");
        }
        
        logger.info("Configuration validation passed");
    }
    
    public Path getInputPath() {
        String inputFile = properties.getProperty("filePath", DEFAULT_INPUT_FILE);
        return Paths.get(inputFile);
    }
    
    public Path getOutputPath() {
        String outputFile = properties.getProperty("outputFile", DEFAULT_OUTPUT_FILE);
        return Paths.get(outputFile);
    }
    
    public double getScaleFactor() {
        String scaleFactorStr = properties.getProperty("scaleFactor", String.valueOf(DEFAULT_SCALE_FACTOR));
        try {
            return Double.parseDouble(scaleFactorStr);
        } catch (NumberFormatException e) {
            logger.warn("Invalid scale factor '{}', using default: {}", scaleFactorStr, DEFAULT_SCALE_FACTOR);
            return DEFAULT_SCALE_FACTOR;
        }
    }
    
    public double getPrecisionFactor() {
        String precisionFactorStr = properties.getProperty("precisionFactor", String.valueOf(DEFAULT_PRECISION_FACTOR));
        try {
            return Double.parseDouble(precisionFactorStr);
        } catch (NumberFormatException e) {
            logger.warn("Invalid precision factor '{}', using default: {}", precisionFactorStr, DEFAULT_PRECISION_FACTOR);
            return DEFAULT_PRECISION_FACTOR;
        }
    }
    
    public String getCharacterSet() {
        return properties.getProperty("characterSet", DEFAULT_CHARACTER_SET);
    }
    
    public ScalingParameters getScalingParameters() {
        return new ScalingParameters(getScaleFactor(), getPrecisionFactor());
    }
    
    public RenderingOptions getRenderingOptions() {
        return new RenderingOptions(getCharacterSet());
    }
}
