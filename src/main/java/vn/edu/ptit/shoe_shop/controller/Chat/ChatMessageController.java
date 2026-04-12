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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/chat")
@RequiredArgsConstructor
public class ChatMessageController {
    private final ConversationService conversationService;
    private final ChatMessageService messageService;
    private final SecurityUtils securityUtil;

    // (Tuỳ chọn) đảm bảo có phòng ngay khi cần
    @PostMapping("/conversations/ensure")
    public ConversationResponse ensureConversation() {
        UUID userId = securityUtil.getCurrentUserId();
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
        UUID userId = securityUtil.getCurrentUserId();
        return messageService.send(req, userId);
    }

    @GetMapping("/messages")
    public Page<ChatMessageResponse> listMessages(
            @RequestParam String conversationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        // đảm bảo page và size hợp lệ
        page = Math.max(page, 0);
        size = Math.min(Math.max(size, 1), 100);

        // chỉ cho phép sort theo createdAt
        if (!"createdAt".equals(sortBy)) {
            sortBy = "createdAt";
        }

        Sort sort = "asc".equalsIgnoreCase(direction)
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        UUID viewerId = securityUtil.getCurrentUserId();
        return messageService.listByConversation(conversationId, viewerId.toString(), pageable);
    }
}
