package ru.nsu.ccfit.kokunina.view;

import java.util.ArrayList;

public class ConsoleClientView implements ClientView {
    @Override
    public void showNewMessage(String newMessage, String from) {
        System.out.println(from + ": " + newMessage);
    }

    @Override
    public void showUserList(ArrayList<String> currentUserList) {
        System.out.println("Currently online:");
        for (String user : currentUserList) {
            System.out.println("*" + user);
        }
    }

    @Override
    public void exit() {
        System.out.println("Goodbye, have a good day!");
    }
}
