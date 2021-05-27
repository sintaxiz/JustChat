package ru.nsu.ccfit.kokunina;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.nsu.ccfit.kokunina.dto.Message;
import ru.nsu.ccfit.kokunina.dto.MessageType;
import ru.nsu.ccfit.kokunina.dto.client.requests.LoginRequest;
import ru.nsu.ccfit.kokunina.dto.client.requests.NewMessage;
import ru.nsu.ccfit.kokunina.dto.exceptions.NameAlreadyTakenException;
import ru.nsu.ccfit.kokunina.dto.exceptions.ReceiveErrorException;
import ru.nsu.ccfit.kokunina.dto.exceptions.SendErrorException;
import ru.nsu.ccfit.kokunina.dto.server.responses.UserMessage;

import java.io.*;
import java.net.Socket;

public class ChatServerClient extends Thread {


    private static final Logger log = LoggerFactory.getLogger(ChatServerClient.class);
    private final Socket socket;
    private final ChatServer server;
    private String userName;

    private final ServerMessagesController messagesController;

    public ChatServerClient(Socket socket, ChatServer server) throws IOException {
        this.socket = socket;
        this.server = server;

        InputStream inputStream = socket.getInputStream();
        DataInputStream socketInput = new DataInputStream(inputStream);

        OutputStream outputStream = socket.getOutputStream();
        DataOutputStream socketOutput = new DataOutputStream(outputStream);

        messagesController = new JsonServerMessagesController(socketInput, socketOutput);

    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            Message clientMessage = null;
            try {
                clientMessage = messagesController.readMessage();
                //log.info("get client message: {}", clientMessage.getMessageBody());
                switch (clientMessage.getType()) {
                    case USER_LIST -> {
                        log.info("Received user list request from {}", this);
                        messagesController.sendUserList(server.getUserList());
                        log.info("Successfully send user list to {}", this);
                    }
                    case NEW_MESSAGE -> {
                        NewMessage newMessage = messagesController.readNewMessage(clientMessage);
                        log.info("Received new message from {}: {}", this, newMessage);
                        server.notifyAllExcept(this, newMessage.getMessage());
                    }
                    default -> log.warn("ChatServerClient don't have handler for message type {}", clientMessage.getType());
                }
            } catch (
                    SendErrorException e) {
                log.error("Can not send message {} to client. Client will be removed.", clientMessage, e);
                break;
            } catch (
                    IOException e) {
                log.error("Can not receive message from client {}. Client will be removed.", this);
                break;
            }

        }
        disconnect();

    }

    private void disconnect() {
        try {
            socket.close();
            log.info("Connection closed: {}", this);
        } catch (IOException e) {
            log.error("Can not close socket {}", this, e);
        } finally {
            server.removeClient(this);
        }
    }

    public void auth() throws IOException, NameAlreadyTakenException, ReceiveErrorException {
        LoginRequest loginRequest;
        loginRequest = messagesController.receiveLogin();
        String loginName = loginRequest.getName();
        if (server.hasUser(loginName)) {
            log.info("Server already has user with name {}. Error message will be send.", loginName);
            messagesController.sendMessage(MessageType.ERROR, "Name already taken.");
            throw new NameAlreadyTakenException();
        }
        messagesController.sendMessage(MessageType.SUCCESS, "Name " + loginName + "accepted.");
        userName = loginName;
        log.info("Client {} get new name: {}", this, userName);
    }

    @Override
    public String toString() {
        return socket.toString();
    }

    public String getUserName() {
        return userName;
    }

    public void receiveMessageFrom(ChatServerClient client, String message) {
        try {
            messagesController.sendUserMessage(new UserMessage(client.getUserName(), message));
        } catch (SendErrorException e) {
            log.error("Can not send new user message to client. Maybe try again?", e);
        }
    }
}
