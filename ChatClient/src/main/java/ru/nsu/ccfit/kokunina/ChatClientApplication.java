package ru.nsu.ccfit.kokunina;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ChatClientApplication {

    private static final Logger log = LoggerFactory.getLogger(ChatClientApplication.class);

    private static final int SERVER_PORT = 666;

    public static void main(String[] args) throws InterruptedException, UnknownHostException {

        final String SERVER_ADDRESS = InetAddress.getLocalHost().getHostAddress();

        // 1) connect to server
        ChatClient chatClient = new ChatClient();
        try {
            chatClient.connect(SERVER_ADDRESS, SERVER_PORT);
            log.info("Connection with server established");
        } catch (IOException e) {
            log.error("Can not establish connection with server", e);
            return;
        }

        // 2) get name from user
        System.out.print("Enter your name: ");
        Scanner in = new Scanner(System.in);
        String userName = in.nextLine();

        // 3) login on server
        try {
            chatClient.logIn(userName);
        } catch (IOException e) {
            log.error("Can not log in", e);
            return;
        }

        // 4) start exchange messages
        chatClient.start();
        System.out.print("""
                Commands:
                /exit -- disconnect and close chat
                /users -- display user list
                """);
        chatClient.join();
    }
}
