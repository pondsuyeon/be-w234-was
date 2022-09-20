package controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.FileUtils;
import webserver.HttpRequest;
import webserver.HttpResponse;
import webserver.StatusCode;

import java.io.IOException;

public class StaticFileController {
    private static final Logger logger = LoggerFactory.getLogger(StaticFileController.class);
    private static StaticFileController instance = new StaticFileController();

    private StaticFileController(){

    }
    public static StaticFileController getInstance(){
        return instance;
    }

    public HttpResponse process(HttpRequest httpRequest) throws IOException {

        if (FileUtils.isStaticFile(httpRequest.getPath())){
            return new HttpResponse.Builder()
                    .statusCode(StatusCode.OK)
                    .body(FileUtils.getBytesFromStaticFilePath(httpRequest.getPath()))
                    .mime(httpRequest.getHeaders().get("Accept"))
                    .build();
        }

        return new HttpResponse.Builder()
                .statusCode(StatusCode.NOT_FOUND)
                .body(FileUtils.getBytesFromStaticFilePath("/error_not_found.html"))
                .build();
    }
}
