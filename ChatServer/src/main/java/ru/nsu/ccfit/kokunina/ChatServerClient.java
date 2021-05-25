package ru.nsu.ccfit.kokunina;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.nsu.ccfit.kokunina.dto.Message;
import ru.nsu.ccfit.kokunina.dto.MessageType;
import ru.nsu.ccfit.kokunina.dto.client.requests.LoginRequest;
import ru.nsu.ccfit.kokunina.dto.exceptions.NameAlreadyTakenException;
import ru.nsu.ccfit.kokunina.dto.exceptions.ReceiveErrorException;
import ru.nsu.ccfit.kokunina.dto.exceptions.SendErrorException;

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
            Message newMessage = null;
            if (socket.isClosed()) {
                log.info("Connection was closed.");
                try {
                    sleep(100000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            try {
                newMessage = messagesController.readMessage();
                if (newMessage.getType() == MessageType.USER_LIST) {
                    log.info("Received user list request from {}", this);
                    messagesController.sendUserList(server.getUserList());
                    log.info("Send user list request to {}", this);
                } else {
                    log.warn("ChatServerClient don't have handler for message type {}", newMessage.getType());
                }
            } catch (SendErrorException e) {
                log.error("Can not send message {} to client", newMessage, e);
                interrupt();
            } catch (IOException e) {
                log.error("Can not receive message from client", e);
                interrupt();
            }
        }
        disconnect();
    }

    private void disconnect() {
        try {
            socket.close();
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
            throw new NameAlreadyTakenException();
        }
        userName = loginName;
        log.info("user name = {}", userName);
    }

    @Override
    public String toString() {
        return socket.toString();
    }

    public String getUserName() {
        return userName;
    }
}
