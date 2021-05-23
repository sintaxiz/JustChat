package ru.nsu.ccfit.kokunina;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ChatServerApplication {

    private static final Logger log = LoggerFactory.getLogger(ChatServerClient.class);

    private static final int SERVER_PORT = 666;

    public static void main(String[] args) {
        try {
            ChatServer chatServer = new ChatServer(SERVER_PORT);
            log.info("Server successfully created");
            chatServer.start();
            log.info("Server successfully started");
            chatServer.join();
        } catch (IOException e) {
            log.error("Can not create chat server: ", e);
        } catch (InterruptedException e) {
            log.error("Thread {} was interrupted: ", Thread.currentThread(), e);
        }
    }
}
