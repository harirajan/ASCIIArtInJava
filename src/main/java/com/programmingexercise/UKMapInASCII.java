package com.programmingexercise;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

/********Assumptions******
 * 1) the lat/long is converted to xy based  user defined scale factor.
 * 2) the x y cordinates are rounded to precision factor to 4 digits excluding the decimal places.
 * 3) the lat long combination is mapped to an imaginary 2D plane based on the above two assumptions.
 */


/**
 * Class to generate the ascii map of UK based on the uk lat long information
 */
public class UKMapInASCII {
    protected static final Logger logger = LogManager.getLogger();


    public static int SCALE_FACTOR = 50;
    public static int PRECISION_FACTOR = 100;

    public static String FILE_PATH = "Asciifile.txt";
    public static String configFile = "./config.properties";
    public static String filePath ="/Users/harirajana/Downloads/test.csv";

    public static int maxLong;
    public static int maxLat;
    public static int minLong;
    public static int minLat;
    public static void main(String[] args) throws IOException {
        boolean fileWritten = false;
        Instant start = Instant.now();

        if(args.length>0) {
            logger.info("read from custom config properties file");
            Properties outsideProperties = new Properties();
            FileInputStream input = new FileInputStream(configFile);
            try {
                outsideProperties.load(input);
                input.close();

            } catch (IOException e) {
                throw new CustomException("Unexpected exception", "UNEXPECTED_EXCEPTION");
            }
            String filePath = outsideProperties.getProperty("filePath");
            PRECISION_FACTOR = Integer.parseInt(outsideProperties.getProperty("precisionFactor"));
            SCALE_FACTOR = Integer.parseInt(outsideProperties.getProperty("scaleFactor"));
            FILE_PATH = outsideProperties.getProperty("outputFile");
        } else {
            logger.info("read from default config properties file");
        }
        logger.info("reading lat long values from file to list");
        List<Coordinates> listOfCoordinates = new ArrayList<>();
        listOfCoordinates = CoordinateUtils.readFileToCoordinates(filePath, listOfCoordinates);
        logger.info("converting lat long to xy cordinates");
        Map<Integer, List<Integer>> map;
        map = CoordinateUtils.convertCoordinatesToXY(listOfCoordinates, new HashMap<>(), SCALE_FACTOR, PRECISION_FACTOR);
        logger.info("converted lat long to xy cordinates");
        logger.info("building the string builder with the map pattern");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder = CoordinateUtils.createStringBuilder(maxLong, minLong, maxLat, minLat, stringBuilder, map);
        logger.info("completed the string builder with the map ascii pattern");

        logger.info("writing the pattern to  to file");
        fileWritten = CoordinateUtils.writeToFile(FILE_PATH, stringBuilder);
        logger.info("generated the file with pattern {}", fileWritten);

        Instant end = Instant.now();
        logger.info("The time taken for file generation is :{}", Duration.between(start, end).toMillis());
    }
}
