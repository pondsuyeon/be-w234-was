package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import controller.StaticFileController;
import controller.UserController;
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

        String path = httpRequest.getPath();
        if (path.startsWith("/user")) {
            httpResponse = UserController.getInstance().process(httpRequest);
        }

        if (httpResponse != null)
            return httpResponse;

        return StaticFileController.getInstance().process(httpRequest);
    }
    private void writeResponse(DataOutputStream dos, HttpResponse httpResponse) {
        responseHeader(dos, httpResponse);
        responseBody(dos, httpResponse);
    }

    private void responseHeader(DataOutputStream dos, HttpResponse httpResponse) {
        try {
            dos.writeBytes(httpResponse.getProtocol() + " " + httpResponse.getStatusCode().getStatus() + " " + httpResponse.getStatusCode().getMessage() + "\r\n");

            for (String key : httpResponse.getHeaders().keySet()){
                String value = httpResponse.getHeaders().get(key);
                dos.writeBytes(key+": " + value+"\r\n");
            }
                dos.writeBytes("\r\n");
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

}