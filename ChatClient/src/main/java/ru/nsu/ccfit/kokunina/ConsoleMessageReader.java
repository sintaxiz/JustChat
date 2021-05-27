package ru.nsu.ccfit.kokunina;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class ConsoleMessageReader implements MessageReader {

    private final Scanner in;

    public ConsoleMessageReader() {
        in = new Scanner(System.in);
    }

    @Override
    public String readMessage() throws IOException {
        String message = in.nextLine()  ;
        if (message == null) {
            throw new IOException("Readline return null string");
        }
        return message;
    }
}
