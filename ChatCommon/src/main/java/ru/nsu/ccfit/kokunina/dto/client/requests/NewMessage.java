package ru.nsu.ccfit.kokunina.dto.client.requests;

public class NewMessage {
    private String message;

    public NewMessage() {
    }

    public NewMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }
}
