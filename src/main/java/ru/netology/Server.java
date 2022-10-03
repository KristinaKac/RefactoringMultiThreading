package ru.netology;

import java.io.*;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Server {
  public static void main(String[] args) {
    final var validPaths = List.of("/index.html", "/spring.svg", "/spring.png",
            "/resources.html", "/styles.css", "/app.js", "/links.html", "/forms.html", "/classic.html",
            "/events.html", "/events.js");

    Callable<Integer> client1 = new Client("Клиент №1");
    Callable<Integer> client2 = new Client("Клиент №2");
    Callable<Integer> client3 = new Client("Клиент №3");
    Callable<Integer> client4 = new Client("Клиент №4");

    final ExecutorService threadPool = Executors.newFixedThreadPool(5);

    List<Future<Integer>> list = new ArrayList<>();
    list.add(threadPool.submit(client1));
    list.add(threadPool.submit(client2));
    list.add(threadPool.submit(client3));
    list.add(threadPool.submit(client4));

    startServer(validPaths);
    threadPool.shutdown();

        }
  public static void startServer(List<String> validPaths) {
    try (final var serverSocket = new ServerSocket(9999)) {
      while (true) {
        try (
                final var socket = serverSocket.accept();
                final var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                final var out = new BufferedOutputStream(socket.getOutputStream());
        ) {
          final var requestLine = in.readLine();
          final var parts = requestLine.split(" ");
          final var path = parts[1];
          final var filePath = Path.of(".", "public", path);
          final var mimeType = Files.probeContentType(filePath);
          if (parts.length != 3) {
            continue;
          }
          checkClassic(path, filePath, mimeType, out);
          containFile(path, validPaths, out);
          checkSize(filePath, mimeType, out);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
  public static void checkClassic(String path, Path filePath, String mimeType, BufferedOutputStream out) throws IOException {
    if (path.equals("/classic.html")) {
      final var template = Files.readString(filePath);
      final var content = template.replace(
              "{time}",
              LocalDateTime.now().toString()
      ).getBytes();
      out.write((
              "HTTP/1.1 200 OK " +
                      "Content-Type: " + mimeType + "\r\n" +
                      "Content-Length: " + content.length + "\r\n" +
                      "Connection: close\r\n" +
                      "\r\n"
      ).getBytes());
      out.write(content);
      out.flush();
    }
  }
  public static void containFile(String path, List<String> validPaths, BufferedOutputStream out) throws IOException {
    if (!validPaths.contains(path)) {
      out.write((
              "HTTP/1.1 404 Not Found\r\n" +
                      "Content-Length: 0\r\n" +
                      "Connection: close\r\n" +
                      "\r\n"
      ).getBytes());
      out.flush();
    }
  }
  public static void checkSize(Path filePath, String mimeType, BufferedOutputStream out) throws IOException {
    final var length = Files.size(filePath);
    out.write((
            "HTTP/1.1 200 OK\r\n" +
                    "Content-Type: " + mimeType + "\r\n" +
                    "Content-Length: " + length + "\r\n" +
                    "Connection: close\r\n" +
                    "\r\n"
    ).getBytes());
    Files.copy(filePath, out);
    out.flush();
  }
}




