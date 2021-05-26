package ru.nsu.ccfit.kokunina.dto.server.responses;

public class UserMessage {
    private String name;
    private String message;

    public UserMessage(String name, String message) {
        this.name = name;
        this.message = message;
    }

    public UserMessage() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
