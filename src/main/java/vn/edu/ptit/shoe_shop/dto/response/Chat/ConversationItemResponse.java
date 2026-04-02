package vn.edu.ptit.shoe_shop.dto.response.Chat;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConversationItemResponse {
    String conversationId;
    String lastMessage;
    Instant updatedAt;
    SenderSummary senderSummary;
}
