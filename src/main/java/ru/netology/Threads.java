package ru.netology;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;


public class Threads {
    public void workingThreads(BufferedReader in, BufferedOutputStream out) throws IOException, InterruptedException {
        while (true) {
            final var requestLine = in.readLine();
            final var parts = requestLine.split(" ");
            final var path = parts[1];

            System.out.println(path);
            final var filePath = Path.of(".", "public", path);
            final var mimeType = Files.probeContentType(filePath);

            final var validPaths = List.of("/index.html", "/spring.svg", "/spring.png",
                    "/resources.html", "/styles.css", "/app.js", "/links.html", "/forms.html", "/classic.html",
                    "/events.html", "/events.js");
            if (!validPaths.contains(path) && (parts.length != 3)) {
                out.write(((
                        "HTTP/1.1 404 Not Found\r\n" +
                                "Content-Length: 0\r\n" +
                                "Connection: close\r\n" +
                                "\r\n"
                ).getBytes()));
                out.flush();
            }

            final var length = Files.size(filePath);
            out.write(((
                    "HTTP/1.1 200 OK\r\n" +
                            "Content-Type: " + mimeType + "\r\n" +
                            "Content-Length: " + length + "\r\n" +
                            "Connection: close\r\n" +
                            "\r\n"
            ).getBytes()));
            Files.copy(filePath, out);
            out.flush();

            if (path.equals("/classic.html")) {
                final var template = Files.readString(filePath);
                final var content = template.replace(
                        "{time}",
                        LocalDateTime.now().toString()
                ).getBytes();
                out.write(((
                        "HTTP/1.1 200 OK " +
                                "Content-Type: " + mimeType + "\r\n" +
                                "Content-Length: " + content.length + "\r\n" +
                                "Connection: close\r\n" +
                                "\r\n"
                ).getBytes()));
                out.write((content));
                out.flush();
            }
        }
    }
}
