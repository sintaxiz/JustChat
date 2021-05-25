package ru.nsu.ccfit.kokunina;

import ru.nsu.ccfit.kokunina.dto.client.requests.LoginRequest;
import ru.nsu.ccfit.kokunina.dto.client.requests.UserListRequest;
import ru.nsu.ccfit.kokunina.dto.exceptions.ReceiveErrorException;
import ru.nsu.ccfit.kokunina.dto.exceptions.SendErrorException;
import ru.nsu.ccfit.kokunina.dto.server.responses.UserList;

public interface ClientMessagesController {
    void sendUserListRequest(UserListRequest userListRequest) throws SendErrorException;

    UserList receiveUserList() throws ReceiveErrorException;

    void sendLoginRequest(LoginRequest loginRequest) throws SendErrorException;
}
