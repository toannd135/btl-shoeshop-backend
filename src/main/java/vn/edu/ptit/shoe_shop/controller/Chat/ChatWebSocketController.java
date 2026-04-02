package vn.edu.ptit.shoe_shop.controller.Chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.edu.ptit.shoe_shop.common.security.SecurityUtils;
import vn.edu.ptit.shoe_shop.dto.request.ChatMessageRequest;
import vn.edu.ptit.shoe_shop.dto.response.Chat.ChatMessageResponse;
import vn.edu.ptit.shoe_shop.service.ChatMessageService;

import java.util.UUID;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatWebSocketController {

    private final ChatMessageService chatMessageService;
    private final SecurityUtils securityUtils;
    // Client sẽ send đến /app/chat.sendMessage
    @MessageMapping("/chat.messages.send")
    public void handleSendMessage(ChatMessageRequest request) {
        try {
            UUID senderId = this.securityUtils.getCurrentUserId();
            ChatMessageResponse resp = chatMessageService.send(request, senderId);
            log.info("Message sent via WS: {}", resp.getMessageId());
        } catch (Exception e) {
            log.error("Error when handling WS message", e);
        }
    }
}
