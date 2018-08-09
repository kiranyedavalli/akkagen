/*
 * Developed  by Kiran Yedavalli on 8/9/18 8:21 AM
 * Last Modified 8/7/18 12:27 PM
 * Copyright (c) 2018. All rights reserved.
 */

package com.akkagen.exceptions;

public class AkkagenException extends RuntimeException {

    private String message;
    private AkkagenExceptionType type;

    public AkkagenException(String message){
        this.message = message;
    }

    public AkkagenException(String message, AkkagenExceptionType type){
        this.message = message;
        this.type = type;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public AkkagenExceptionType getType() {
        return type;
    }

    public void setType(AkkagenExceptionType type) {
        this.type = type;
    }
}
