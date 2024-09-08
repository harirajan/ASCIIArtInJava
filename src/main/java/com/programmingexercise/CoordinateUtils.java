package com.programmingexercise;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class contains all the utility methods
 */

public class CoordinateUtils {
    protected static final Logger logger = LogManager.getLogger();
    /**
     * This method converts the passed map of lat long combination to a map of x,y cordinates
     * @param listOfCoordinates list of coordinates
     * @param returnMap map of x, y coordinates
     * @return  returns the x,y coordinates
     */
    public static HashMap<Integer, List<Integer>> convertCoordinatesToXY(List<Coordinates> listOfCoordinates, HashMap<Integer, List<Integer>> returnMap, int scaleFactor, int precisionFactor) {
        int initialMinValue = 0;
        Map<Double, Double> tempMap = new HashMap<Double, Double>();
        for(Coordinates coordinates: listOfCoordinates){
            tempMap.put(coordinates.getLatitude(), coordinates.getLongitude());
        }
        double longitudeValue = 0;
        double latitudeValue = 0;

        for (Map.Entry entry : tempMap.entrySet()) {
            latitudeValue = (((double) entry.getKey() * scaleFactor) / 180) + ((double) scaleFactor / 2);
            longitudeValue = ((double) entry.getValue() * scaleFactor / 360) + ((double) scaleFactor / 2);
            int roundLong = (int) Math.round(longitudeValue * precisionFactor);

            int roundLat = (int) Math.round(latitudeValue * precisionFactor);

            //find the max and min values for lat and long
            if (initialMinValue == 0) {
                UKMapInASCII.minLat = roundLat;
                UKMapInASCII.minLong = roundLong;
            }
            if (UKMapInASCII.minLat > roundLat) {
                UKMapInASCII.minLat = roundLat;
            }
            if (UKMapInASCII.maxLat < roundLat) {
                UKMapInASCII.maxLat = roundLat;
            }
            if (UKMapInASCII.minLong > roundLong) {
                UKMapInASCII.minLong = roundLong;
            }
            if (UKMapInASCII.maxLong < roundLong) {
                UKMapInASCII.maxLong = roundLong;
            }
            if (returnMap.get(roundLat) == null || returnMap.get(roundLat).isEmpty()) {
                List<Integer> longList = new ArrayList<>();
                longList.add(roundLong);
                returnMap.put(roundLat, longList);
            } else {
                returnMap.get(roundLat).add(roundLong);
            }
            initialMinValue++;
        }
        return returnMap;
    }

    /**
     *
     * @param filePath path of the file from which the coordinates are to be read
     * @return listOfCoordinates containing the list of coordinates
     */
    public static List<Coordinates> readFileToCoordinates(String filePath) throws IOException {
        List<Coordinates> listOfCoordinates = new ArrayList<Coordinates>();
        FileReader inputputFile = null;
        try {
            inputputFile = new FileReader(filePath);
        } catch (FileNotFoundException e) {
            throw new CustomException("Specified file is not found", "FILE_NOT_FOUND");
        }
        BufferedReader br = new BufferedReader(inputputFile);
        String line = null;

        Coordinates coordinates = null;
        try {
            br.readLine();


            while (true) {

                if ((line = br.readLine()) == null) break;

                String str[] = line.split(",");
                coordinates = new Coordinates();
                coordinates.setLatitude(Double.parseDouble(str[2]));
                coordinates.setLongitude(Double.parseDouble(str[3]));
                listOfCoordinates.add(coordinates);

            }
        } catch (IOException e) {
            throw new CustomException("Exception while reading the file", "FILE_ERROR");
        } finally{
            br.close();
        }
        return listOfCoordinates;
    }

    /**
     *
     * @param filePath file to which the writing is to be done
     * @param stringBuilder contains the pattern to be written
     * @return true or false based on the file is written or not
     */
    public static boolean writeToFile(String filePath, StringBuilder stringBuilder){
        File outputFile = new File(filePath);
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(outputFile));
            writer.append(stringBuilder);
            return true;
        } catch (IOException e) {
            throw new CustomException("Exception while writing to file", "FILE_ERROR");
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    throw new CustomException("Exception while closing the buff writer", "BUFF_CLOSE_ERROR");
                } finally {
                    logger.info("file closed successfully");
                }
            }
        }
    }

    public static StringBuilder createStringBuilder(int maxLong, int minLong, int maxLat, int minLat,   Map<Integer, List<Integer>> map){
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = minLat; i <= maxLat; i++) {
            for (int j = minLong; j <= maxLong; j++) {
                if (map.containsKey(i)) {
                    if (map.get(i).contains(j)) {
                        if(i%2==0)
                            stringBuilder.append("u");
                        else
                            stringBuilder.append("k");
                    } else {
                        stringBuilder.append("  ");
                    }
                } else {
                    stringBuilder.append("  ");
                }

            }
            stringBuilder.append("\n");
        }
        return stringBuilder;
    }
}
