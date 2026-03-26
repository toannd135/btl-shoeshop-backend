package vn.edu.ptit.shoe_shop.service;

import java.util.List;

import vn.edu.ptit.shoe_shop.dto.request.ChatMessageRequest;
import vn.edu.ptit.shoe_shop.dto.response.Chat.ChatMessageResponse;

public interface ChatMessageService {
    public ChatMessageResponse send(ChatMessageRequest req);
    public List<ChatMessageResponse> listByConversation(String conversationId, String viewerId);
}
