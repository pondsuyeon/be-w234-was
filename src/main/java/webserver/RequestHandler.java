package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
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
            BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            DataOutputStream dos = new DataOutputStream(out);

            HttpRequest httpRequest = RequestParser.getHttpRequestFromInput(br);
            HttpResponse httpResponse = processRequest(httpRequest);

            writeResponse(dos, httpResponse);

        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private HttpResponse processRequest(HttpRequest httpRequest) throws IOException {

        HttpResponse httpResponse = null;
        String requestHeaderAccept = httpRequest.getHeaders().get("Accept");

        if (isStaticFile(httpRequest.getPath())) {
            httpResponse = new HttpResponse(StatusCode.OK, getBytesFromFilePath(httpRequest.getPath()), requestHeaderAccept);
        } else if (httpRequest.getPath().equals("/user/create")) {

            UserController userController = UserController.getInstance();

            if (httpRequest.getMethod() == Method.GET) {
                userController.createUserWithGet(httpRequest);
            } else if (httpRequest.getMethod() == Method.POST) {
                userController.createUserWithPost(httpRequest);
            }

            httpResponse = new HttpResponse(StatusCode.FOUND, "http://"+httpRequest.getHeaders().get("Host")+"/index.html", requestHeaderAccept);
        } else {
            httpResponse = new HttpResponse(StatusCode.NOT_FOUND, getBytesFromFilePath("/error_not_found.html"), requestHeaderAccept);
        }

        return httpResponse;
    }
    private void writeResponse(DataOutputStream dos, HttpResponse httpResponse) {
        responseHeader(dos, httpResponse);
        responseBody(dos, httpResponse);
    }

    private void responseHeader(DataOutputStream dos, HttpResponse httpResponse) {
        try {
            dos.writeBytes(httpResponse.getProtocol() + " " + httpResponse.getStatusCode().getStatus() + " " + httpResponse.getStatusCode().getMessage() + "\r\n");

            if (httpResponse.getStatusCode() == StatusCode.OK) {
                dos.writeBytes("Content-Type: " + httpResponse.getHeaders().get("Content-Type") + ";charset=utf-8\r\n");
                dos.writeBytes("Content-Length: " + httpResponse.getBody().length + "\r\n");
                dos.writeBytes("\r\n");
            } else if (httpResponse.getStatusCode() == StatusCode.FOUND) {
                dos.writeBytes("Location: "+ httpResponse.getRedirectUrl());
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, HttpResponse httpResponse) {
        try {
            dos.write(httpResponse.getBody());
            dos.flush();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private boolean isStaticFile(String path) {
        File file = new File("./webapp" + path);
        return file.exists() && file.isFile();
    }

    private byte[] getBytesFromFilePath(String path) throws IOException {
        return Files.readAllBytes(new File("./webapp" + path).toPath());
    }
}