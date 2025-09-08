package com.programmingexercise.improved;

/**
 * Immutable class holding rendering options for ASCII art generation.
 * Improvements over the original implementation:
 * - Configurable character sets for different artistic effects
 * - Input validation
 * - Immutable design for thread safety
 */
public class RenderingOptions {
    private final String characterSet;
    
    public RenderingOptions(String characterSet) {
        if (characterSet == null || characterSet.isEmpty()) {
            throw new IllegalArgumentException("Character set cannot be null or empty");
        }
        
        this.characterSet = characterSet;
    }
    
    public String getCharacterSet() {
        return characterSet;
    }
    
    @Override
    public String toString() {
        return String.format("RenderingOptions{characterSet='%s'}", characterSet);
    }
}
