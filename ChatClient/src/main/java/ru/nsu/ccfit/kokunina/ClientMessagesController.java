package ru.nsu.ccfit.kokunina;

import ru.nsu.ccfit.kokunina.dto.Message;
import ru.nsu.ccfit.kokunina.dto.client.requests.LoginRequest;
import ru.nsu.ccfit.kokunina.dto.client.requests.UserListRequest;
import ru.nsu.ccfit.kokunina.dto.client.requests.NewMessage;
import ru.nsu.ccfit.kokunina.dto.exceptions.ReceiveErrorException;
import ru.nsu.ccfit.kokunina.dto.exceptions.SendErrorException;
import ru.nsu.ccfit.kokunina.dto.server.responses.UserList;
import ru.nsu.ccfit.kokunina.dto.server.responses.UserMessage;

import java.io.IOException;

public interface ClientMessagesController {
    void sendUserListRequest(UserListRequest userListRequest) throws SendErrorException;

    void sendLoginRequest(LoginRequest loginRequest) throws SendErrorException;

    Message readMessage() throws IOException;

    void sendUserMessage(NewMessage newMessage) throws SendErrorException;

    UserMessage readUserMessage(Message serverMessage) throws IOException;

    UserList readUserList(Message serverMessage) throws IOException;
}
