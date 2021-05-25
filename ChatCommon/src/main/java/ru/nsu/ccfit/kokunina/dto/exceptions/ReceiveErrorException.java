package ru.nsu.ccfit.kokunina.dto.exceptions;

public class ReceiveErrorException extends Exception {
    public ReceiveErrorException() {
        super();
    }

    public ReceiveErrorException(String message) {
        super(message);
    }

    public ReceiveErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReceiveErrorException(Throwable cause) {
        super(cause);
    }
}
