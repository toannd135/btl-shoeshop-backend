package vn.edu.ptit.shoe_shop.service;



import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.edu.ptit.shoe_shop.dto.request.ChatMessageRequest;
import vn.edu.ptit.shoe_shop.dto.response.Chat.ChatMessageResponse;

public interface ChatMessageService {
    public ChatMessageResponse send(ChatMessageRequest req,UUID senderId);
    Page<ChatMessageResponse> listByConversation(String conversationId, String viewerId, Pageable pageable);
}