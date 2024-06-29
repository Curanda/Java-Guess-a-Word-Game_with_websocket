package WORKERS;

import CLIENTS.ServerClient;
import CLIENTS.ServerClientWorker;
import HELPERS.MessageHelper;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ServerWorker {
    private static final int SERVER_PORT = 2002;
    private final List<ServerClient> clients = new ArrayList<>();
    private ServerSocket serverSocket;
    private ServerClientWorker gameWorker;
    private ServerClient currentChooser;
    private boolean gameInProgress = false;

    public void initialize() {
        System.out.println("Serwer nasłuchuje na porcie " + SERVER_PORT);
        try {
            serverSocket = new ServerSocket(SERVER_PORT);
            gameWorker = new ServerClientWorker(this);
            gameWorker.start();

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Nowe połączenie: " + socket.getInetAddress().getHostAddress());
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                ServerClient client = new ServerClient(socket, writer, reader);
                clients.add(client);
                System.out.println("Klient dodany. Liczba klientów: " + clients.size());

                if (!gameInProgress) {
                    startNewGame();
                } else {
                    client.sendMessage(new MessageHelper(MessageHelper.GAME_STATUS, "Dołączyłeś do gry. Czekaj na swoją kolej zgadywania.").serialize());
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void startNewGame() {
        if (clients.size() > 0) {
            currentChooser = clients.get(new Random().nextInt(clients.size()));
            gameWorker.setChooser(currentChooser);
            gameInProgress = false;  // Set to false until the word is chosen

            for (ServerClient client : clients) {
                if (client == currentChooser) {
                    client.sendMessage(new MessageHelper(MessageHelper.CHOOSE_WORD, "Jesteś wybierającym. Wybierz słowo:").serialize());
                } else {
                    client.sendMessage(new MessageHelper(MessageHelper.GAME_STATUS, "Nowa gra rozpoczęta! Czekaj, aż zostanie wybrane słowo.").serialize());
                }
            }
        }
    }

    public boolean isGameInProgress() {
        return gameInProgress;
    }

    public void broadcastMessage(String message) {
        for (ServerClient client : clients) {
            client.sendMessage(message);
        }
    }

    public List<ServerClient> getClients() {
        return clients;
    }

    public void setGameInProgress(boolean inProgress) {
        this.gameInProgress = inProgress;
    }
}
