package vn.edu.ptit.shoe_shop.controller.Chat;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.edu.ptit.shoe_shop.dto.response.Chat.ConversationItemResponse;
import vn.edu.ptit.shoe_shop.service.ConversationService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/conversations")
@RequiredArgsConstructor
public class ConversationController {
    private final ConversationService conversationService;
    @GetMapping()
    public List<ConversationItemResponse> listConversations(@RequestParam String viewerId) {
        return conversationService.listConversationsForViewer(viewerId.toString());
    }
}
