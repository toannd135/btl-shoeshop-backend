package vn.edu.ptit.shoe_shop.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import vn.edu.ptit.shoe_shop.common.exception.IdInvalidException;
import vn.edu.ptit.shoe_shop.common.exception.NotFoundException;
import vn.edu.ptit.shoe_shop.dto.request.ChatMessageRequest;
import vn.edu.ptit.shoe_shop.dto.response.Chat.ChatMessageResponse;
import vn.edu.ptit.shoe_shop.dto.response.Chat.SenderSummary;
import vn.edu.ptit.shoe_shop.entity.Conversation;
import vn.edu.ptit.shoe_shop.entity.Message;
import vn.edu.ptit.shoe_shop.entity.User;
import vn.edu.ptit.shoe_shop.repository.ChatMessageRepository;
import vn.edu.ptit.shoe_shop.repository.ConversationRepository;
import vn.edu.ptit.shoe_shop.repository.UserRepository;
import vn.edu.ptit.shoe_shop.service.ChatMessageService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatMessageServiceImpl implements ChatMessageService {
    final ConversationRepository conversationRepository;
    final ChatMessageRepository chatMessageRepository;
    final UserRepository userRepository;
    final ConversationServiceImpl conversationServiceImpl;
    final SimpMessagingTemplate messagingTemplate;

    public SenderSummary toSenderSummary(User user) {
        return SenderSummary.builder()
                .senderId(user.getUserId().toString())
                .avatar(user.getAvatarImage())
                .senderName(user.getFirstName() + " " + user.getLastName())
                .build();
    }

    private ChatMessageResponse toMessageResponse(Message m, String currentUserId) {
        return ChatMessageResponse.builder()
                .messageId(m.getMessageId().toString())
                .conversationId(m.getConversation().getConversationId().toString())
                .senderId(m.getSender().getUserId().toString())
                .senderSummary(toSenderSummary(m.getSender()))
                .content(m.getContent())
                .me(Objects.equals(m.getSender().getUserId().toString(), currentUserId))
                .createdAt(m.getCreatedAt())
                .build();
    }

    @Transactional
    public ChatMessageResponse send(ChatMessageRequest req, UUID senderID) {
        if (req.getContent() == null || req.getContent().isBlank()) {
            throw new IllegalArgumentException("Content is blank");
        }

        final String ADMIN_ID = ConversationServiceImpl.DEFAULT_ADMIN_ID;
        User sender = userRepository.findByUserId(senderID).orElseThrow(
                () -> new NotFoundException("User not found"));

        UUID userId;
        if (Objects.equals(sender.getUserId().toString(), ADMIN_ID)) {
            if (req.getReceiverId() == null) {
                throw new IllegalArgumentException("Admin must specify receiverId");
            }
            userId = UUID.fromString(req.getReceiverId());
        } else {
            userId = sender.getUserId();
        }

        Conversation conv = conversationServiceImpl.addConversation(userId);

        Message m = new Message();
        m.setConversation(conv);
        m.setSender(sender);
        m.setContent(req.getContent());
        chatMessageRepository.save(m);

        conversationRepository.updateLastMessage(conv.getConversationId(), req.getContent());

        // User sends message -> mark as unread for admin
        // Admin sends message -> keep read status (admin just interacted)
        boolean isSenderAdmin = Objects.equals(sender.getUserId().toString(), ADMIN_ID);
        if (!isSenderAdmin) {
            conversationRepository.markAsUnreadByAdmin(conv.getConversationId());
        }

        ChatMessageResponse response = toMessageResponse(m, sender.getUserId().toString());

        // Per-conversation broadcast (for client already open in this conversation)
        messagingTemplate.convertAndSend("/topic/conversation/" + conv.getConversationId(), response);

        // Global broadcast: all admins receive to update conversationsList realtime
        messagingTemplate.convertAndSend("/topic/chat", response);

        return response;
    }

    @Transactional(readOnly = true)
    public Page<ChatMessageResponse> listByConversation(String conversationId, String viewerId, Pageable pageable) {
        UUID conversationIdUUID;
        try {
            conversationIdUUID = UUID.fromString(conversationId);
        } catch (IllegalArgumentException e) {
            throw new IdInvalidException("Invalid UUID format");
        }

        // pageable chứa cả filter (conversationId) và Sort (DESC/ASC)
        // Dùng method có filter + pageable để Spring Data hiểu đúng
        Page<Message> page = chatMessageRepository
                .findByConversationConversationId(conversationIdUUID, pageable);

        return page.map(m -> toMessageResponse(m, viewerId));
    }
}
