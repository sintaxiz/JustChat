package ru.nsu.ccfit.kokunina;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.nsu.ccfit.kokunina.dto.Message;
import ru.nsu.ccfit.kokunina.dto.MessageType;
import ru.nsu.ccfit.kokunina.dto.client.requests.Login;

import java.io.*;
import java.net.Socket;

public class ChatServerClient extends Thread {


    private static final Logger log = LoggerFactory.getLogger(ChatServerClient.class);
    private final Socket socket;
    private final ChatServer server;
    private String userName;

    public ChatServerClient(Socket socket, ChatServer server) {
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        // 1. Authentication with client to create login and session ID
        try {
            auth();
            log.info("Successful authentication: {}", this);
        } catch (IOException e) {
            log.error("Unsuccessful authentication: {}. Thread will be stopped.", this);
            return;
        }
        /*while (!isInterrupted()) {
            try () {
                String msg = objectInputStream.readUTF();
                while (!msg.equals("bye")) {
                    log.info("{} receive message: {}", this, msg);
                    msg = objectInputStream.readUTF();
                }
            } catch (IOException e) {
                log.error("Can not receive message (maybe connection is closed?):", e);
            }
        }*/
        try {
            sleep(1000000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            socket.close();
        } catch (IOException e) {
            log.info("Can not close socket {}", this, e);
        } finally {
            server.removeClient(this);
        }
    }

    private void auth() throws IOException {
        InputStream inputStream = socket.getInputStream();
        DataInputStream dataInputStream = new DataInputStream(inputStream);

        // 1) Get login message as json
        String loginMessageJson = dataInputStream.readUTF();
        log.info("login msg = {}", loginMessageJson);

        // 2) Map json message to message object
        ObjectMapper objectMapper = new ObjectMapper();
        Message loginMessage = objectMapper.readValue(loginMessageJson, Message.class);

        // 2.1) Check is it correct message type
        if (loginMessage.getType() != MessageType.LOGIN) {
            log.error("Wrong message type from client {}. Requested LOGIN but received {} ",
                                                    this, loginMessage.getType());
            // todo: exception
            return;
        }

        // 3) Get login as json and map it
        String loginJson = loginMessage.getMessageBody();
        Login login = objectMapper.readValue(loginJson, Login.class);

        String loginName = login.getName();

        if (server.hasUser(loginName)) {
            log.info("Server already has user with name {}. Error message will be send.", loginName);
            return;
            // TODO: send error to client
        }
        userName = loginName;
        log.info("user name = {}", userName);
    }

    @Override
    public String toString() {
        return socket.toString();
    }

    public OutputStream getOutputStream() throws IOException {
        return socket.getOutputStream();
    }

    public String getUserName() {
        return userName;
    }
}
