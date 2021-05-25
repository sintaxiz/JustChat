package ru.nsu.ccfit.kokunina;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.nsu.ccfit.kokunina.dto.Message;
import ru.nsu.ccfit.kokunina.dto.MessageType;
import ru.nsu.ccfit.kokunina.dto.User;
import ru.nsu.ccfit.kokunina.dto.client.requests.LoginRequest;
import ru.nsu.ccfit.kokunina.dto.exceptions.ReceiveErrorException;
import ru.nsu.ccfit.kokunina.dto.exceptions.SendErrorException;
import ru.nsu.ccfit.kokunina.dto.server.responses.UserList;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class JsonServerMessagesController implements ServerMessagesController {

    private final DataInputStream input;
    private final DataOutputStream output;
    private final ObjectMapper objectMapper;

    public JsonServerMessagesController(DataInputStream input, DataOutputStream output) {
        this.input = input;
        this.output = output;
        objectMapper = new ObjectMapper();
    }

    @Override
    public void sendUserList(ArrayList<User> users) throws SendErrorException {
        UserList userList = new UserList(users);
        String userListString;
        try {
            userListString = objectMapper.writeValueAsString(userList);
            sendMessage(MessageType.USER_LIST, userListString);
        } catch (IOException e) {
            throw new SendErrorException("Can not write user list to output", e);
        }
    }

    @Override
    public LoginRequest receiveLogin() throws ReceiveErrorException {
        // 1) Get login message as json
        String loginMessageJson;
        try {
            loginMessageJson = input.readUTF();
        } catch (IOException e) {
            throw new ReceiveErrorException("Can not read utf from input.", e);
        }

        // 2) Map json message to message object
        ObjectMapper objectMapper = new ObjectMapper();
        Message loginMessage;
        try {
            loginMessage = objectMapper.readValue(loginMessageJson, Message.class);
        } catch (IOException e) {
            throw new ReceiveErrorException("Can not convert string message to message object", e);
        }

        // 2.1) Check is it correct message type
        if (loginMessage.getType() != MessageType.LOGIN) {
            throw new ReceiveErrorException("Wrong message type from client. Requested LOGIN but received " +
                    loginMessage.getType());
        }

        // 3) Get login as json and map it
        String loginJson = loginMessage.getMessageBody();
        LoginRequest loginRequest;
        try {
            loginRequest = objectMapper.readValue(loginJson, LoginRequest.class);
        } catch (IOException e) {
            throw new ReceiveErrorException("Can not convert message body to LoginRequest object.", e);
        }
        return loginRequest;
    }


    public Message readMessage() throws IOException {
        String messageString = input.readUTF();
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(messageString, Message.class);
    }

    public void sendMessage(MessageType type, String body) throws IOException {
        Message message = new Message(type, body);
        String messageString = objectMapper.writeValueAsString(message);
        output.writeUTF(messageString);
        output.flush();
    }
}
