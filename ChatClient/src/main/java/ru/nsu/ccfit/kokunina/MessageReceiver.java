package ru.nsu.ccfit.kokunina;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.nsu.ccfit.kokunina.dto.Message;
import ru.nsu.ccfit.kokunina.dto.User;
import ru.nsu.ccfit.kokunina.dto.exceptions.ReceiveErrorException;
import ru.nsu.ccfit.kokunina.dto.server.responses.UserList;
import ru.nsu.ccfit.kokunina.dto.server.responses.UserMessage;
import ru.nsu.ccfit.kokunina.view.ClientView;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class MessageReceiver extends Thread {

    private static final Logger log = LoggerFactory.getLogger(MessageReceiver.class);

    private final ClientMessagesController messagesController;
    private final ClientView view;

    public MessageReceiver(ClientMessagesController messagesController, ClientView view) {
        this.messagesController = messagesController;
        this.view = view;
    }


    @Override
    public void run() {
        while (!isInterrupted()) {
            try {
                Message serverMessage = messagesController.readMessage();
                switch (serverMessage.getType()) {
                    case USER_MESSAGE -> {
                        UserMessage userMessage = messagesController.readUserMessage(serverMessage);
                        view.showNewMessage(userMessage.getMessage(), userMessage.getName());
                    }
                    case USER_LIST -> {
                        UserList userList = messagesController.readUserList(serverMessage);
                        ArrayList<User> users = userList.getUsers();
                        ArrayList<String> usersStringList = new ArrayList<>();
                        for (User user : users) {
                            usersStringList.add(user.getName());
                        }
                        view.showUserList(usersStringList);
                    }
                    case NEW_USER_ADDED -> view.showNewUserNotification(serverMessage.getMessageBody());
                    default -> log.warn("MessageReceiver don't have handler for this message type: {}", serverMessage.getType());
                }
            } catch (IOException e) {
                log.warn("Can not read message. Probably connection was closed.");
                return;
            }
        }
    }
}
