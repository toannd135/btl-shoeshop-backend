package vn.edu.ptit.shoe_shop.controller.Chat;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.edu.ptit.shoe_shop.dto.request.ChatMessageRequest;
import vn.edu.ptit.shoe_shop.dto.response.Chat.ChatMessageResponse;
import vn.edu.ptit.shoe_shop.dto.response.Chat.ConversationResponse;
import vn.edu.ptit.shoe_shop.entity.Conversation;
import vn.edu.ptit.shoe_shop.service.ChatMessageService;
import vn.edu.ptit.shoe_shop.service.ConversationService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatMessageController {
    private final ConversationService conversationService;
    private final ChatMessageService messageService;

    // (Tuỳ chọn) đảm bảo có phòng ngay khi cần
    @PostMapping("/conversations/ensure")
    public ConversationResponse ensureConversation(@RequestParam String userId) {
        Conversation c = conversationService.addConversation(userId);
        return ConversationResponse.builder()
                .conversationId(c.getConversationId().toString())
                .userId(c.getUser().getUserId().toString())
                .adminId(c.getAdmin().getUserId().toString())
                .lastMessage(c.getLastMessage())
                .updatedAt(c.getUpdatedAt())
                .build();
    }

    // Gửi tin: user → admin mặc định, hoặc admin → user (bằng targetUserId)
    @PostMapping("/messages")
    public ChatMessageResponse sendMessage(@RequestBody ChatMessageRequest req) {
        return messageService.send(req);
    }

    @GetMapping("/messages")
    public List<ChatMessageResponse> listMessages(
            @RequestParam String conversationId,
            @RequestParam String viewerId // prod: lấy từ JWT
    ) {
        return messageService.listByConversation(conversationId, viewerId);
    }
}
