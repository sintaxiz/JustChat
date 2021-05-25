package ru.nsu.ccfit.kokunina.dto.exceptions;

public class SendErrorException extends Exception{
    public SendErrorException() {
        super();
    }

    public SendErrorException(String message) {
        super(message);
    }

    public SendErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public SendErrorException(Throwable cause) {
        super(cause);
    }
}
