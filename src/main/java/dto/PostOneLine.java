package dto;

import lombok.*;
import model.Post;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostOneLine {

    private Long id;
    private PostWriter writer;
    private String content;
    private LocalDateTime createdAt;

    public static PostOneLine of(Post post){
        return PostOneLine.builder()
                .id(post.getId())
                .writer(PostWriter.of(post.getWriter()))
                .content(post.getContent())
                .createdAt(post.getCreatedAt())
                .build();
    }
}
