package ru.nsu.ccfit.kokunina.view;

import java.util.ArrayList;

public interface ClientView {
    void showNewMessage(String newMessage, String from);

    void showUserList(ArrayList<String> currentUserList);

    void exit();

}
