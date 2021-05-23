package ru.nsu.ccfit.kokunina.dto;

/**
 * Class wrapper for requests and responses
 */
public class Message {
    private MessageType type;
    // message body as json-string
    private String messageBody;

    public Message() {
    }

    public Message(MessageType type, String messageBody) {
        this.type = type;
        this.messageBody = messageBody;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }
}
