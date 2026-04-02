package vn.edu.ptit.shoe_shop.dto.response.Chat;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatMessageResponse {
    String messageId;
    String conversationId;
    String senderId;
    String content;
    boolean me;
    String createdAt;
    SenderSummary senderSummary;

}
