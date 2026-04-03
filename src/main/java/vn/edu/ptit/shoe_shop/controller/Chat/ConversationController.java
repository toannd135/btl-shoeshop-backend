package vn.edu.ptit.shoe_shop.controller.Chat;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.edu.ptit.shoe_shop.common.security.SecurityUtils;
import vn.edu.ptit.shoe_shop.dto.response.Chat.ConversationItemResponse;
import vn.edu.ptit.shoe_shop.dto.response.Chat.ListConversationForAdminResponse;
import vn.edu.ptit.shoe_shop.service.ConversationService;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/conversations")
@RequiredArgsConstructor
public class ConversationController {
    private final ConversationService conversationService;
    private final SecurityUtils securityUtil;
    @GetMapping()
    public ListConversationForAdminResponse getAllConversations() {
        UUID viewerId = securityUtil.getCurrentUserId();
        return conversationService.listConversationsForAdmin(viewerId);
    }
}
