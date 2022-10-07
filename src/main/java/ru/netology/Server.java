package ru.netology;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server extends Thread {
    private final ExecutorService threadPool = Executors.newFixedThreadPool(64);
    ServerThread serverThread = new ServerThread();

    @Override
    public void run() {
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(9999);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        while (true) {
            try {
                var socket = serverSocket.accept();
                threadPool.submit(() -> {
                    try {
                        serverThread.processing(socket);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            } catch (IOException ioe) {
                ioe.printStackTrace();

            }
        }
    }
}





