package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import model.User;
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

            HttpRequest httpRequest = getHttpRequestFromInput(br);
            byte[] body = processRequest(httpRequest);

            response200Header(dos, body.length, httpRequest);
            responseBody(dos, body);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private HttpRequest getHttpRequestFromInput(BufferedReader br) throws IOException {
        String startLine = br.readLine();
        List<String> headerLines = new ArrayList<>();

        String line = "";
        while (true) {
            line = br.readLine();
            if (line == null || "".equals(line)) break;
            headerLines.add(line);
        }
        return RequestParser.getHttpRequestFromInput(startLine, headerLines);
    }

    // TODO 메서드 명 변경
    private byte[] processRequest(HttpRequest httpRequest) throws IOException {

        if (httpRequest.getPath().equals("/user/create")){
            String userId = httpRequest.getParameters().get("userId");
            String password = httpRequest.getParameters().get("password");
            String name = httpRequest.getParameters().get("name");
            String email = httpRequest.getParameters().get("email");

            User user = new User(userId, password, name, email);

            logger.debug("CreateUserRequest UserInfo: {}", user);
        }

        return getBytesFromFilePath(httpRequest.getPath());
    }
    private byte[] getBytesFromFilePath(String path) throws IOException {
        try {
            return Files.readAllBytes(new File("./webapp" + path).toPath());
        } catch (Exception e) {
           return Files.readAllBytes(new File("./webapp" + "/error_not_found.html").toPath());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent, HttpRequest httpRequest) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: "+getContentTypeByRequest(httpRequest)+";charset=utf-8\r\n");
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

    private String getContentTypeByRequest(HttpRequest httpRequest) {
        // TODO 추후에 정적 파일이 아닐 경우 따로 처리 필요
        String ext = httpRequest.getPath().substring(httpRequest.getPath().lastIndexOf(".") + 1);

        switch (ext){
            case "html":
                return "text/html";
            case "css":
                return "text/css";
            default:
                return "text/html";
        }
    }
}