package com.org.ResolveIt.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ErrorResponse {
    private LocalDateTime timestamp;
    private String message;
    private String details;
    private int status;
    private String path;

    public ErrorResponse(LocalDateTime timestamp, String message, String details, int status, String path) {
        super();
        this.timestamp = timestamp;
        this.message = message;
        this.details = details;
        this.status = status;
        this.path = path;
    }

    public ErrorResponse(String message) {
        super();
        this.message = message;
    }
}
