package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            DataOutputStream dos = new DataOutputStream(out);

            byte[] body = new byte[8192];
            String line = "";
            int lineCount = 0;

            logger.debug("--------Request Header--------");

            while (true) {
                line = br.readLine();
                if (line == null || "".equals(line)) break;

                logger.debug("Line: {}", line);

                if (lineCount == 0) {
                    String path = line.split(" ")[1];
                    logger.debug("Path: {}", path);

                    body = getBytesFromStaticFile(path);
                }

                lineCount++;
            }

            response200Header(dos, body.length);
            responseBody(dos, body);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private byte[] getBytesFromStaticFile(String path) throws IOException {
        byte[] bytes = new byte[8192];
        try {
            bytes = Files.readAllBytes(new File("./webapp" + path).toPath());
        } catch (Exception e) {
            bytes = Files.readAllBytes(new File("./webapp" + "/error_not_found.html").toPath());
        }

        return bytes;
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}