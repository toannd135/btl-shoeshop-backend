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
public class ConversationResponse {
    String conversationId;
    String userId;
    String adminId;
    String lastMessage;
    LocalDateTime updatedAt;
    LocalDateTime createdAt;
}
