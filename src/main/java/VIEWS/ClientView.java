package VIEWS;

import WORKERS.ClientWorker;
import HELPERS.MessageHelper;

import javax.swing.*;
import java.awt.*;

public class ClientView extends JFrame {
    private JTextArea gameArea;
    private JTextField inputField;
    private JButton sendButton;
    private ClientWorker clientWorker;
    private boolean isChooser = false;
    private boolean canGuess = false;

    public ClientView(String playerID) {
        setTitle(playerID);
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        gameArea = new JTextArea();
        gameArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(gameArea);

        inputField = new JTextField();
        sendButton = new JButton("Send");

        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(inputField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

        sendButton.addActionListener(e -> sendMessage());
        inputField.addActionListener(e -> sendMessage());

        clientWorker = new ClientWorker(this);
        clientWorker.start();

        setVisible(true);
    }

    private void sendMessage() {
        String message = inputField.getText().trim();
        if (!message.isEmpty()) {
            String messageType;
            if (isChooser && !canGuess) {
                messageType = MessageHelper.CHOOSE_WORD;
            } else if (canGuess) {
                messageType = MessageHelper.GUESS_WORD;
            } else {
                gameArea.append("Poczekaj na swoją kolej.\n");
                return;
            }
            clientWorker.sendMessage(messageType, message);
            inputField.setText("");
        }
    }

    public void addMessage(String message) {
        gameArea.append(message + "\n");
        gameArea.setCaretPosition(gameArea.getDocument().getLength());

        if (message.contains("Jesteś wybierającym")) {
            isChooser = true;
            canGuess = false;
        } else if (message.contains("Zacznijcie zgadywać")) {
            canGuess = true;
        } else if (message.contains("Nowa gra rozpoczęta")) {
            isChooser = false;
            canGuess = false;
        }
    }
}
