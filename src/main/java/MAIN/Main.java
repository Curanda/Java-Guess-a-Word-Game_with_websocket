package MAIN;

import WORKERS.ServerWorker;
import VIEWS.ClientView;
import WORKERS.ClientWorker;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        new Thread(() -> {
            ServerWorker server = new ServerWorker();
            server.initialize();
        }).start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Scanner scanner = new Scanner(System.in);
        System.out.println("Podaj liczbę graczy");
        int clientCount = scanner.nextInt();

        for (int i = 1; i <= clientCount; i++) {
            ClientView clientView = new ClientView("Gracz "+i);
        }
    }
}