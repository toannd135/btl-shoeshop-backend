package vn.edu.ptit.shoe_shop.controller.Chat;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.edu.ptit.shoe_shop.common.security.SecurityUtils;
import vn.edu.ptit.shoe_shop.dto.request.ChatMessageRequest;
import vn.edu.ptit.shoe_shop.dto.response.Chat.ChatMessageResponse;
import vn.edu.ptit.shoe_shop.dto.response.Chat.ConversationResponse;
import vn.edu.ptit.shoe_shop.entity.Conversation;
import vn.edu.ptit.shoe_shop.service.ChatMessageService;
import vn.edu.ptit.shoe_shop.service.ConversationService;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatMessageController {
    private final ConversationService conversationService;
    private final ChatMessageService messageService;
    private final SecurityUtils securityUtils;

    // (Tuỳ chọn) đảm bảo có phòng ngay khi cần
    @PostMapping("/conversations/ensure")
    public ConversationResponse ensureConversation() {
        UUID userID = this.securityUtils.getCurrentUserId();
        Conversation c = conversationService.addConversation(userID.toString());
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
        UUID senderId = this.securityUtils.getCurrentUserId();
        return messageService.send(req, senderId);
    }

    @GetMapping("/messages")
    public List<ChatMessageResponse> listMessages(
            @RequestParam String conversationId
    ) {
        UUID viewerId = this.securityUtils.getCurrentUserId();
        return messageService.listByConversation(conversationId, viewerId.toString());
    }
}
