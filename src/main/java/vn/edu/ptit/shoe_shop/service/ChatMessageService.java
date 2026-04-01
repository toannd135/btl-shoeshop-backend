package vn.edu.ptit.shoe_shop.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.edu.ptit.shoe_shop.dto.request.ChatMessageRequest;
import vn.edu.ptit.shoe_shop.dto.response.Chat.ChatMessageResponse;

public interface ChatMessageService {
    ChatMessageResponse send(ChatMessageRequest req);
    Page<ChatMessageResponse> listByConversation(String conversationId, String viewerId, Pageable pageable);
}
