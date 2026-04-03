package vn.edu.ptit.shoe_shop.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.edu.ptit.shoe_shop.dto.response.Chat.ChatMessageResponse;
import vn.edu.ptit.shoe_shop.dto.response.Chat.ConversationItemResponse;
import vn.edu.ptit.shoe_shop.dto.response.Chat.ListConversationForAdminResponse;
import vn.edu.ptit.shoe_shop.entity.Conversation;

public interface ConversationService {

    Conversation addConversation(UUID userId);

    List<ConversationItemResponse> listConversationsForViewer(UUID viewerId);

    ListConversationForAdminResponse listConversationsForAdmin(UUID adminId);
}