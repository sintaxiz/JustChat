package ru.nsu.ccfit.kokunina;

public class ConsoleClientView implements ClientView {
    @Override
    public void showNewMessage(String newMessage, String from) {
        System.out.println(from + ": " + newMessage);
    }
}
