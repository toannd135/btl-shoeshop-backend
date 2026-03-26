package vn.edu.ptit.shoe_shop.service;

import java.util.List;

import vn.edu.ptit.shoe_shop.dto.response.Chat.ConversationItemResponse;
import vn.edu.ptit.shoe_shop.entity.Conversation;

public interface ConversationService {

    public Conversation addConversation(String userId);
    public List<ConversationItemResponse> listConversationsForViewer(String viewerId);

}
