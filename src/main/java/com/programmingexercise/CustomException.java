package com.programmingexercise;

/**
 * Custom Exception class
 */
public class CustomException extends RuntimeException{
    private String errorCode;

    public CustomException(String message, String errorCode){
        super(message);
        this.errorCode=errorCode;

    }
}
