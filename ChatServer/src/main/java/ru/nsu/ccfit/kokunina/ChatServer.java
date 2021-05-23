package ru.nsu.ccfit.kokunina;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
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
                addClient(newClient);
                //notifyClients();
                newClient.start();

            } catch (IOException e) {
                log.error("Exception caught while accepting socket", e);
            }
        }
    }

    public synchronized void notifyClients() {
        for (ChatServerClient client : clients) {
            OutputStreamWriter outputStreamWriter;
            try {
                outputStreamWriter = new OutputStreamWriter(client.getOutputStream());
            } catch (IOException e) {
                log.error("can not get output stream from client {}", client, e);
                return;
            }
            try {
                outputStreamWriter.write("hello!");
                log.info("Send hello to {}", client);
            } catch (IOException e) {
                log.error("can not send hello to client {}", client, e);
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
            log.info("CLIENT name = {}, cLIENTSize= {}", client.getUserName(), clients.size());
            if (userName.equals(client.getUserName())) {
                return true;
            }
        }
        return false;
    }

}
