package ru.nsu.ccfit.kokunina;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.nsu.ccfit.kokunina.dto.Message;
import ru.nsu.ccfit.kokunina.dto.MessageType;
import ru.nsu.ccfit.kokunina.dto.User;
import ru.nsu.ccfit.kokunina.dto.client.requests.LoginRequest;
import ru.nsu.ccfit.kokunina.dto.client.requests.NewMessage;
import ru.nsu.ccfit.kokunina.dto.client.requests.UserListRequest;
import ru.nsu.ccfit.kokunina.dto.exceptions.ReceiveErrorException;
import ru.nsu.ccfit.kokunina.dto.exceptions.SendErrorException;
import ru.nsu.ccfit.kokunina.dto.server.responses.UserList;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ChatClient extends Thread {

    private static final Logger log = LoggerFactory.getLogger(ChatClient.class);

    private Socket socket;

    private ClientMessagesController messagesController;

    public ChatClient() {
    }

    @Override
    public void run() {

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

        MessageReceiver messageReceiver = new MessageReceiver(socket, messagesController, new ConsoleClientView());
        messageReceiver.start();

        while (!isInterrupted()) {
            String userMessage;
            try {
                userMessage = bufferedReader.readLine();
            } catch (IOException e) {
                log.error("Can not read message from console");
                break;
            }
            try {
                messagesController.sendUserMessage(new NewMessage(userMessage));
                log.info("User message successfully sent");
            } catch (SendErrorException e) {
                log.error("Can not send user message", e);
                break;
            }
        }

        try {
            messagesController.sendUserListRequest(new UserListRequest(1));
            log.info("Request on user list was sent");
        } catch (SendErrorException e) {
            log.error("Can not send user list request", e);
            return;
        }
        try {
            UserList userList = messagesController.receiveUserList();
            ArrayList<User> users = userList.getUsers();
            for (User user : users) {
                log.info("user name = {}", user);
            }
        } catch (ReceiveErrorException e) {
            log.error("can not get user list", e);
        }

        try {
            disconnect();
        } catch (IOException e) {
            log.error("Can not disconnect", e);
        }
    }

    public void connect(String address, int serverPort) throws IOException {
        socket = new Socket(address, serverPort);
        DataOutputStream socketOutput = new DataOutputStream(socket.getOutputStream());
        DataInputStream socketInput = new DataInputStream(socket.getInputStream());
        messagesController = new JsonClientMessagesController(socketInput, socketOutput);
    }

    public void disconnect() throws IOException {
        if (socket == null) {
            log.error("Can not disconnect because socket is null");
            return;
        }
        socket.close(); // Closing this socket will also close the socket's InputStream and OutputStream.
    }
   /* public void sendMessage(ArrayList<String> messages) {
        if (socket == null) {
            log.error("Can not send message because socket is null. Use connect() to create socket.");
            return;
        }
        try (OutputStream outputStream = socket.getOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream)) {
            for (String msg : messages) {
                objectOutputStream.writeUTF(msg);
                log.info("Client sent message \"{}\" to server", msg);
            }
        } catch (IOException e) {
            log.error("Can not send message", e);
        }
    }*/

    public void logIn(String loginName) throws IOException {
        log.info("Trying log in with name {}...", loginName);
        if (socket == null) {
            log.error("Can not log in because socket is null. Use connect() to create socket.");
            return;
        }
        try {
            messagesController.sendLoginRequest(new LoginRequest(loginName));
            log.info("Authentication message successfully send to server");
        } catch (SendErrorException e) {
            throw new IOException("Can not send login request.", e);
        }
        Message serverAnswer = messagesController.readMessage();
        MessageType answerType = serverAnswer.getType();
        switch (answerType) {
            case SUCCESS -> log.info("Successfully login");
            case ERROR -> {
                log.error("Can not login. Error answer from server: {}", serverAnswer.getMessageBody());
                throw new IOException();
            }
            default -> {
                log.error("Wrong answer type. Expected ERROR or SUCCESS, but received: {}", answerType);
                throw new IOException("can not log in");
            }
        }
    }

}
