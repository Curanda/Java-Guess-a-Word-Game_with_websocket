package CLIENTS;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerClient {
    private static int nextId = 1;
    private final int id;
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;

    public ServerClient(Socket socket, PrintWriter writer, BufferedReader reader) {
        this.id = nextId++;
        this.socket = socket;
        this.writer = writer;
        this.reader = reader;
    }

    public int getId() {
        return id;
    }

    public Socket getSocket() {
        return socket;
    }

    public PrintWriter getWriter() {
        return writer;
    }

    public BufferedReader getReader() {
        return reader;
    }

    public void sendMessage(String message) {
        writer.println(message);
        writer.flush();
    }

    public String readMessage() throws IOException {
        return reader.readLine();
    }
}
