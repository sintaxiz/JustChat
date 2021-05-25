package ru.nsu.ccfit.kokunina.dto.server.responses;

public class Success {
    private String message;

    public Success() {

    }
    
    public Success(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
