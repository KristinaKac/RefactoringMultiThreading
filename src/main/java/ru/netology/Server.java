package ru.netology;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;

public class Server extends Thread{

    @Override
    public void run(){
        try (final var serverSocket = new ServerSocket(9999)) {
            while (true) {
                try (
                        final var socket = serverSocket.accept();
                        final var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        final var out = new BufferedOutputStream(socket.getOutputStream()))
                {
                    ServerThread serverThread = new ServerThread();
                    serverThread.processing(in, out);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}





