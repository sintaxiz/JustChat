package ru.nsu.ccfit.kokunina;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetAddress;

public class ChatClientApplication {

    private static final Logger log = LoggerFactory.getLogger(ChatClientApplication.class);

    private static final int SERVER_PORT = 666;

    public static void main(String[] args) throws InterruptedException {
        ChatClient chatClient = new ChatClient();
        try {
            chatClient.connect(InetAddress.getLocalHost().getHostAddress(), SERVER_PORT);
            log.info("Connection with server established");
        } catch (IOException e) {
            log.error("Can not establish connection with server", e);
            return;
        }
        try {
            chatClient.logIn();
        } catch (IOException e) {
            log.error("Can not log in", e);
        }
        chatClient.start();
        chatClient.join();
    }
}
