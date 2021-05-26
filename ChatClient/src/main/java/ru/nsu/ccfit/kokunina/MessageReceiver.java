package ru.nsu.ccfit.kokunina;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.nsu.ccfit.kokunina.dto.Message;
import ru.nsu.ccfit.kokunina.dto.exceptions.SendErrorException;
import ru.nsu.ccfit.kokunina.dto.server.responses.UserMessage;

import java.io.IOException;
import java.net.Socket;

public class MessageReceiver extends Thread {

    private static final Logger log = LoggerFactory.getLogger(MessageReceiver.class);

    private final Socket socket;
    private final ClientMessagesController messagesController;
    private final ClientView view;

    public MessageReceiver(Socket socket, ClientMessagesController messagesController, ClientView view) {
        this.socket = socket;
        this.messagesController = messagesController;
        this.view = view;
    }


    @Override
    public void run() {
        while (!isInterrupted()) {
            Message serverMessage;

            try {
                serverMessage = messagesController.readMessage();
                //log.info("get server message: {}", serverMessage.getMessageBody());
                switch (serverMessage.getType()) {
                    case USER_MESSAGE -> {
                        UserMessage userMessage = messagesController.readUserMessage(serverMessage);
                        view.showNewMessage(userMessage.getMessage(), userMessage.getName());
                    }
                    default -> log.warn("MessageReceiver don't have handler for this message type: {}", serverMessage.getType());
                }
            } catch (IOException e) {
                log.error("can not read message:", e);
                return;
            }

        }
    }
}
