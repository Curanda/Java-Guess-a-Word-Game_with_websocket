package CLIENTS;

import HELPERS.MessageHelper;
import WORKERS.ServerWorker;

import java.io.IOException;

public class ServerClientWorker extends Thread {
    private final ServerWorker serverWorker;
    private String wordToGuess;
    private ServerClient chooser;


    public ServerClientWorker(ServerWorker serverWorker) {
        this.serverWorker = serverWorker;
    }

    @Override
    public void run() {
        try {
            while (true) {
                for (ServerClient client : serverWorker.getClients()) {
                    if (client.getReader().ready()) {
                        String line = client.readMessage();
                        if (line != null) {
                            handleMessage(client, MessageHelper.deserialize(line));
                        }
                    }
                }
                Thread.sleep(100);
            }
        } catch (IOException | InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    private void handleMessage(ServerClient sender, MessageHelper message) {
        switch (message.getType()) {
            case MessageHelper.CHOOSE_WORD:
                if (sender == chooser && !serverWorker.isGameInProgress()) {
                    wordToGuess = message.getMessage().toLowerCase().trim();
                    System.out.println("Word to guess set: " + wordToGuess);
                    serverWorker.broadcastMessage(new MessageHelper(MessageHelper.GAME_STATUS, "Słowo zostało wybrane. Zacznijcie zgadywać!").serialize());
                    serverWorker.setGameInProgress(true);
                }
                break;
            case MessageHelper.GUESS_WORD:
                if (!serverWorker.isGameInProgress()) {
                    sender.sendMessage(new MessageHelper(MessageHelper.GAME_STATUS, "Gra jeszcze się nie rozpoczęła. Poczekaj, aż chooser wybierze słowo.").serialize());
                    return;
                }
                String guessedWord = message.getMessage().toLowerCase().trim();
                if (sender != chooser) {
                    if (guessedWord.equals(wordToGuess)) {
                        serverWorker.broadcastMessage(new MessageHelper(MessageHelper.GAME_OVER, "Gracz " + sender.getId() + " zgadł słowo: " + wordToGuess).serialize());
                        serverWorker.setGameInProgress(false);
                        serverWorker.startNewGame();
                    } else {
                        sender.sendMessage(new MessageHelper(MessageHelper.GAME_STATUS, "Niepoprawnie. Spróbuj ponownie.").serialize());
                        chooser.sendMessage(new MessageHelper(MessageHelper.GAME_STATUS, "Gracz " + sender.getId() + " zgadywał: " + guessedWord).serialize());
                    }
                } else {
                    sender.sendMessage(new MessageHelper(MessageHelper.GAME_STATUS, "Jesteś wybierającym. Nie możesz zgadywać.").serialize());
                }
                break;
        }}

    public void setChooser(ServerClient chooser) {
        this.chooser = chooser;
        this.wordToGuess = null;
    }
}