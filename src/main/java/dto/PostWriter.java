package dto;

import lombok.*;
import model.User;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostWriter {

    private String userId;
    private String name;

    public static PostWriter of(User user){
        return PostWriter.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .build();
    }
}
