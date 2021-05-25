package ru.nsu.ccfit.kokunina;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.nsu.ccfit.kokunina.dto.User;
import ru.nsu.ccfit.kokunina.dto.exceptions.NameAlreadyTakenException;
import ru.nsu.ccfit.kokunina.dto.exceptions.ReceiveErrorException;
import ru.nsu.ccfit.kokunina.dto.server.responses.UserList;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ChatServer extends Thread {

    private static final Logger log = LoggerFactory.getLogger(ChatServer.class);

    private final ServerSocket serverSocket;
    private final List<ChatServerClient> clients;

    public ChatServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        clients = new LinkedList<>();
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            try {
                Socket newConnection = serverSocket.accept();
                log.info("New connection {} established", newConnection.getRemoteSocketAddress());

                ChatServerClient newClient = new ChatServerClient(newConnection, this);
                try {
                    newClient.auth();
                    log.info("Successful authentication: {}", newClient);
                } catch (IOException | NameAlreadyTakenException | ReceiveErrorException e) {
                    log.error("Unsuccessful authentication: {}.",this, e);
                    return;
                }

                addClient(newClient);
                newClient.start();
                //notifyClients();
            } catch (IOException e) {
                log.error("Exception caught while accepting socket", e);
            }
        }
    }

    public synchronized void removeClient(ChatServerClient client) {
        clients.remove(client);
    }

    public synchronized void addClient(ChatServerClient client) {
        clients.add(client);
    }

    public synchronized boolean hasUser(String userName) {
        for (ChatServerClient client : clients) {
            if (userName.equals(client.getUserName())) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<User> getUserList() {
        if (clients.size() <= 0) {
            return null;
        }
        ArrayList<User> users = new ArrayList<>();
        for (ChatServerClient client : clients) {
            users.add(new User(client.getUserName()));
        }
        return users;
    }
}
