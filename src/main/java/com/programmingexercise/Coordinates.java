package com.programmingexercise;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
/**
 * Coordinates class representing the lat long information
 */
public class Coordinates {

    private double latitude;
    private double longitude;


}
