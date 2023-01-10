package ru.nsu.ccfit.kokunina;

public class ServerConfiguration {
    private final int serverPort;

    public static final ServerConfiguration DefaultConfiguration =
            new ServerConfiguration(666);

    public ServerConfiguration(int serverPort) {
        this.serverPort = serverPort;
    }

    public int getServerPort() {
        return serverPort;
    }
}
