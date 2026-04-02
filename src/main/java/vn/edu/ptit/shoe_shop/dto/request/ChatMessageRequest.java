package vn.edu.ptit.shoe_shop.dto.request;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageRequest {
    private String senderId;
    private String receiverId;
    private String content;
}
