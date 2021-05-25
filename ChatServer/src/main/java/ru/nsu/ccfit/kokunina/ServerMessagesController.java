package ru.nsu.ccfit.kokunina;

import ru.nsu.ccfit.kokunina.dto.Message;
import ru.nsu.ccfit.kokunina.dto.User;
import ru.nsu.ccfit.kokunina.dto.client.requests.LoginRequest;
import ru.nsu.ccfit.kokunina.dto.exceptions.ReceiveErrorException;
import ru.nsu.ccfit.kokunina.dto.exceptions.SendErrorException;

import java.io.IOException;
import java.util.ArrayList;

public interface ServerMessagesController {

    void sendUserList(ArrayList<User> userList) throws SendErrorException;

    LoginRequest receiveLogin() throws ReceiveErrorException;

    Message readMessage() throws IOException;
}
