package ru.nsu.ccfit.kokunina.dto.exceptions;

public class NameAlreadyTakenException extends Exception {
    public NameAlreadyTakenException() {
    }

    public NameAlreadyTakenException(String message) {
        super(message);
    }

    public NameAlreadyTakenException(String message, Throwable cause) {
        super(message, cause);
    }

    public NameAlreadyTakenException(Throwable cause) {
        super(cause);
    }
}
