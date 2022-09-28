package controller;

import webserver.HttpRequest;
import webserver.HttpResponse;

import java.io.IOException;

public interface Controller {

    HttpResponse process(HttpRequest httpRequest) throws IOException;

    HttpResponse processGetRequest(HttpRequest httpRequest) throws IOException;

    HttpResponse processPostRequest(HttpRequest httpRequest);
}