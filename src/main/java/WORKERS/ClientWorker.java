package WORKERS;

import VIEWS.ClientView;
import HELPERS.MessageHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientWorker extends Thread {
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;
    private ClientView clientView;

    public ClientWorker(ClientView clientView) {
        this.clientView = clientView;
        try {
            socket = new Socket("localhost", 2002);
            writer = new PrintWriter(socket.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                MessageHelper message = MessageHelper.deserialize(line);
                clientView.addMessage(message.getMessage());
                if (message.getType().equals(MessageHelper.CLOSE)) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(String type, String message) {
        String serializedMessage = new MessageHelper(type, message).serialize();
        writer.println(serializedMessage);
        writer.flush();
    }
}
