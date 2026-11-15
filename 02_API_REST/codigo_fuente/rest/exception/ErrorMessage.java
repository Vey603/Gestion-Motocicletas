package com.gestion_motocicletas.rest.exception;

import lombok.Getter;

import java.util.Date;

@Getter
public class ErrorMessage {

    private final int statusCode;
    private final Date date;
    private final String message;
    private final String description;

    public ErrorMessage(int statusCode, Date date, String message, String description) {
        this.statusCode = statusCode;
        this.date = date;
        this.message = message;
        this.description = description;
    }
}
