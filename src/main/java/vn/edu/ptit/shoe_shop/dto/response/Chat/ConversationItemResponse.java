package vn.edu.ptit.shoe_shop.dto.response.Chat;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

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
    /**
     * Admin đã đọc cuộc trò chuyện này chưa.
     * true = đã đọc, false = chưa đọc (frontend hiện badge đỏ).
     */
    boolean isRead;
}
