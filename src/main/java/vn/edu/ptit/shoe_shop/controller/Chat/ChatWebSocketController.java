package vn.edu.ptit.shoe_shop.controller.Chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.edu.ptit.shoe_shop.dto.request.ChatMessageRequest;
import vn.edu.ptit.shoe_shop.dto.response.Chat.ChatMessageResponse;
import vn.edu.ptit.shoe_shop.service.ChatMessageService;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatWebSocketController {

    private final ChatMessageService chatMessageService;

    // Client sẽ send đến /app/chat.sendMessage
    @MessageMapping("/chat.messages.send")
    public void handleSendMessage(ChatMessageRequest request) {
        try {
            ChatMessageResponse resp = chatMessageService.send(request);
            log.info("Message sent via WS: {}", resp.getMessageId());
        } catch (Exception e) {
            log.error("Error when handling WS message", e);
        }
    }
}
