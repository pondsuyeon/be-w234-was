package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.PostForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.PostService;
import webserver.HttpRequest;
import webserver.HttpResponse;
import webserver.Method;
import webserver.StatusCode;

import java.io.IOException;

public class PostController implements Controller{

    private static final Logger logger = LoggerFactory.getLogger(PostController.class);
    private static final PostController instance = new PostController();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private PostController(){
    }
    public static PostController getInstance(){
        return instance;
    }

    @Override
    public HttpResponse process(HttpRequest httpRequest) throws IOException {
        HttpResponse httpResponse = null;
        Method method = httpRequest.getMethod();

        switch (method) {

            case GET: {
                httpResponse = processGetRequest(httpRequest);
                break;
            }
            case POST: {
                httpResponse = processPostRequest(httpRequest);
                break;
            }

            default:
                httpResponse = null;
        }

        return httpResponse;
    }

    @Override
    public HttpResponse processGetRequest(HttpRequest httpRequest) throws IOException {

        HttpResponse httpResponse = null;
        String path = httpRequest.getPath().replaceFirst("/post", "");

        switch (path){
            case "/list": {
                httpResponse = getAllPostList(httpRequest);
                break;
            }
            default:
                httpResponse = null;
        }

        return httpResponse;
    }

    @Override
    public HttpResponse processPostRequest(HttpRequest httpRequest) {

        HttpResponse httpResponse = null;
        String path = httpRequest.getPath().replaceFirst("/post", "");

        switch (path) {
            case "/create": {
                httpResponse = createPost(httpRequest);
                break;
            }
            default:
                httpResponse = null;
        }

        return httpResponse;
    }

    private HttpResponse createPost(HttpRequest httpRequest) {

        PostForm postForm = objectMapper.convertValue(httpRequest.getBody(), PostForm.class);

        PostService postService = PostService.getInstance();

        postService.createPost(postForm);

        return new HttpResponse.Builder()
                .statusCode(StatusCode.FOUND)
                .headers("Location", "http://" + httpRequest.getHeaders().get("Host") + "/index.html")
                .mime(httpRequest.getHeaders().get("Accept"))
                .build();
    }

    private HttpResponse getAllPostList(HttpRequest httpRequest) throws IOException {
        return new HttpResponse.Builder()
                .statusCode(StatusCode.OK)
                .body(objectMapper.writeValueAsBytes(PostService.getInstance().getPostOneLineList()))
                .build();
    }
}
