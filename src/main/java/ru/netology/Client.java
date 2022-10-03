package ru.netology;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.Callable;

public class Client implements Callable<Integer> {
    String[] paths = new String[]{"/index.html", "/spring.svg",
            "/resources.html", "/styles.css", "/app.js", "/links.html", "/forms.html", "/classic.html",
            "/events.html", "/events.js"};
    String name;
    int amountOfClient = 5;
    int result;
    String host = "localhost";
    int port = 9999;

    public Client(String name) {
        this.name = name;
    }

    @Override
    public Integer call() {
        try {
            for (int i = 0, j = 0; i <= amountOfClient; i++, j++) {
                for (int k = 0; k < paths.length; k++) {
                    try (Socket clientSocket = new Socket(host, port);
                         PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                         BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
                        out.println("GET " + paths[k] + " HTTP/1.1");
                        System.out.println(paths[k]);
                        String resp = in.readLine();
                        System.out.println(resp);
                        result = j++;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}