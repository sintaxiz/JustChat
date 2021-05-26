package ru.nsu.ccfit.kokunina;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.nsu.ccfit.kokunina.dto.Message;
import ru.nsu.ccfit.kokunina.dto.MessageType;
import ru.nsu.ccfit.kokunina.dto.client.requests.LoginRequest;
import ru.nsu.ccfit.kokunina.dto.client.requests.UserListRequest;
import ru.nsu.ccfit.kokunina.dto.client.requests.NewMessage;
import ru.nsu.ccfit.kokunina.dto.exceptions.ReceiveErrorException;
import ru.nsu.ccfit.kokunina.dto.exceptions.SendErrorException;
import ru.nsu.ccfit.kokunina.dto.server.responses.UserList;
import ru.nsu.ccfit.kokunina.dto.server.responses.UserMessage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class JsonClientMessagesController implements ClientMessagesController {
    private final DataInputStream input;
    private final DataOutputStream output;
    private final ObjectMapper objectMapper;

    public JsonClientMessagesController(DataInputStream input, DataOutputStream output) {
        this.input = input;
        this.output = output;
        objectMapper = new ObjectMapper();
    }

    @Override
    public void sendUserListRequest(UserListRequest userListRequest) throws SendErrorException {
        try {
            Message message = new Message(MessageType.USER_LIST, objectMapper.writeValueAsString(userListRequest));
            String messageString = objectMapper.writeValueAsString(message);
            output.writeUTF(messageString);
            output.flush();
        } catch (IOException e) {
            throw new SendErrorException("Can not write UserListRequest to output", e);
        }
    }

    @Override
    public UserList receiveUserList() throws ReceiveErrorException {
        String userListMessageJson;
        try {
            userListMessageJson = input.readUTF();
        } catch (IOException e) {
            throw new ReceiveErrorException("Can not read utf from input.", e);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        Message userMessage;
        try {
            userMessage = objectMapper.readValue(userListMessageJson, Message.class);
        } catch (IOException e) {
            throw new ReceiveErrorException("Can not convert string message to message object", e);
        }

        if (userMessage.getType() != MessageType.USER_LIST) {
            throw new ReceiveErrorException("Wrong message type from client. Requested LOGIN but received " +
                    userMessage.getType());
        }

        String userJson = userMessage.getMessageBody();
        UserList userList;
        try {
            userList = objectMapper.readValue(userJson, UserList.class);
        } catch (IOException e) {
            throw new ReceiveErrorException("Can not convert message body to UserList object.", e);
        }
        return userList;
    }

    @Override
    public void sendLoginRequest(LoginRequest loginRequest) throws SendErrorException {
        String loginMessageBody;
        try {
            loginMessageBody = objectMapper.writeValueAsString(loginRequest);
        } catch (IOException e) {
            throw new SendErrorException("Can not parse login to json string", e);
        }

        Message message = new Message(MessageType.LOGIN, loginMessageBody);
        sendMessage(message);
    }

    @Override
    public void sendUserMessage(NewMessage newMessage) throws SendErrorException {
        String userMessageBody;
        try {
            userMessageBody = objectMapper.writeValueAsString(newMessage);
        } catch (IOException e) {
            throw new SendErrorException("Can not parse user message to json string", e);
        }

        Message message = new Message(MessageType.NEW_MESSAGE, userMessageBody);
        sendMessage(message);
    }

    @Override
    public UserMessage readUserMessage(Message serverMessage) throws IOException {
        return objectMapper.readValue(serverMessage.getMessageBody(), UserMessage.class);
    }

    private void sendMessage(Message message) throws SendErrorException {
        String messageString;
        try {
            messageString = objectMapper.writeValueAsString(message);
        } catch (IOException e) {
            throw new SendErrorException("Can not parse loginMessage to json string", e);
        }
        try {
            output.writeUTF(messageString);
            output.flush();
        } catch (IOException e) {
            throw new SendErrorException("Can not write login message to output.");
        }
    }

    @Override
    public Message readMessage() throws IOException {
        String messageString = input.readUTF();
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(messageString, Message.class);
    }
}
