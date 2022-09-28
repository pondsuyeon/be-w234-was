package service;

import dto.PostForm;
import dto.PostOneLine;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.PostRepository;
import repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;


public class PostService {

    private static final Logger logger = LoggerFactory.getLogger(PostService.class);

    private static final PostService instance = new PostService();
    private static final PostRepository postRepository = PostRepository.getInstance();
    private static final UserRepository userRepository = UserRepository.getInstance();
    private PostService(){
    }

    public static PostService getInstance(){
        return instance;
    }

    public void createPost(PostForm postForm){
        User writer = userRepository.findUserById(postForm.getWriterId());

        postRepository.createPost(postForm, writer);
    }

    public List<PostOneLine> getPostOneLineList() {
        return postRepository.findAll().stream().map(PostOneLine::of).collect(Collectors.toList());
    }

}
