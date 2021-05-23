package ru.nsu.ccfit.kokunina;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.nsu.ccfit.kokunina.dto.Message;
import ru.nsu.ccfit.kokunina.dto.MessageType;
import ru.nsu.ccfit.kokunina.dto.client.requests.Login;

import java.io.*;
import java.net.Socket;

public class ChatClient extends Thread {

    private static final Logger log = LoggerFactory.getLogger(ChatClient.class);

    private Socket socket;
    private DataOutputStream socketOutput;
    private DataInputStream socketInput;

    public ChatClient() {
    }

    @Override
    public void run() {
        try {
            sleep(1000000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            disconnect();
        } catch (IOException e) {
            log.error("Can not disconnect", e);
        }
    }

    public void connect(String address, int serverPort) throws IOException {
        socket = new Socket(address, serverPort);
        socketOutput = new DataOutputStream(socket.getOutputStream());
        socketInput = new DataInputStream(socket.getInputStream());
    }

    public void disconnect() throws IOException {
        if (socket == null) {
            log.error("Can not disconnect because socket is null");
            return;
        }
        socket.close(); // Closing this socket will also close the socket's InputStream and OutputStream.
    }

    public void readMessage() {
        if (socket == null) {
            log.error("Can not read message, because socket is null. Use connect() to create socket.");
            return;
        }
        try (InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream())) {
            char[] buff = new char[4];
            int countChars = inputStreamReader.read(buff, 0, 1);
            if (countChars > 0) {
                log.info("Get message from server: {}", buff);
            }
            log.info("No message from server :(");
        } catch (IOException e) {
            log.error("can not read message", e);
        }
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

    public void logIn() throws IOException {
        if (socket == null) {
            log.error("Can not log in because socket is null. Use connect() to create socket.");
            return;
        }

        // 1) Login object to json
        ObjectMapper objectMapper = new ObjectMapper();
        Login login = new Login("test");
        String loginMessageBody;
        try {
            loginMessageBody = objectMapper.writeValueAsString(login);
        } catch (IOException e) {
            log.error("Can not parse login {} to json string", login, e);
            throw e;
        }

        // 2) Wrap login to message
        Message message = new Message(MessageType.LOGIN, loginMessageBody);

        // 3) Message to json
        String loginMessage;
        try {
            loginMessage = objectMapper.writeValueAsString(message);
        } catch (IOException e) {
            log.error("Can not parse login {} to json string", login, e);
            throw e;
        }

        // 4) Send message to server
        socketOutput.writeUTF(loginMessage);
        socketOutput.flush();
        log.info("Message {} successfully write to server", loginMessage);
    }

}
