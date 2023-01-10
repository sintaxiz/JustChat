package ru.nsu.ccfit.kokunina;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ChatServerApplication {

    private static final Logger log = LoggerFactory.getLogger(ChatServerApplication.class);

    public static void main(String[] args) throws InterruptedException {
        var config = ServerConfiguration.DefaultConfiguration;
        try {
        ChatServer chatServer = new ChatServer(config.getServerPort());
            log.info("Server successfully created");
            chatServer.start();
            log.info("Server successfully started");
            chatServer.join();
        } catch (IOException e) {
            log.error("Can not create chat server: ", e);
        }
    }
}
