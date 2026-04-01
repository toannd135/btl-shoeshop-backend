package vn.edu.ptit.shoe_shop.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatMessageServiceImpl implements ChatMessageService {
    private final ConversationRepository conversationRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final ConversationServiceImpl conversationServiceImpl;
    private final SimpMessagingTemplate messagingTemplate;


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
                .createdAt(m.getCreatedAt().toString())
                .build();
    }

    @Transactional
    public ChatMessageResponse send(ChatMessageRequest req) {
        if (req.getContent() == null || req.getContent().isBlank()) {
            throw new IllegalArgumentException("Nội dung tin nhắn trống");
        }

        final String ADMIN_ID = ConversationServiceImpl.DEFAULT_ADMIN_ID;
        User sender = userRepository.findByUserId(UUID.fromString(req.getSenderId())).orElseThrow(
            () -> new NotFoundException("User not found")
        );

        final String userId;
        if (Objects.equals(sender.getUserId().toString(), ADMIN_ID)) {
            // Admin gửi: bắt buộc targetUserId
            if (req.getReceiverId() == null) {
                throw new IllegalArgumentException("Admin gửi tin phải chỉ định targetUserId");
            }
            userId = req.getReceiverId();
        } else {
            // User gửi: luôn chat với admin cố định
            userId = sender.getUserId().toString();
        }

        // Luôn đảm bảo có phòng user <-> admin
        Conversation conv = conversationServiceImpl.addConversation(userId);

        // Lưu message
        Message m = new Message();
        m.setConversation(conv);
        m.setSender(sender);
        m.setContent(req.getContent());
        // createdAt do @CreationTimestamp lo
        chatMessageRepository.save(m);

        // update conversation nhanh hơn
        conversationRepository.updateLastMessage(
                conv.getConversationId(),
                req.getContent()
        );

        //public socket event to client is conversation
        ChatMessageResponse response = toMessageResponse(m, sender.getUserId().toString());
        messagingTemplate.convertAndSend(
                "/topic/conversation/" + conv.getConversationId(),
                response
        );

        // “currentUserId” để set cờ me — ở đây mình coi người gửi là current
        return response;
    }

    @Transactional(readOnly = true)
    public Page<ChatMessageResponse> listByConversation(String conversationId, String viewerId, Pageable pageable) {
        UUID conversationIdUUID;
        try {
            conversationIdUUID = UUID.fromString(conversationId);
        } catch (IllegalArgumentException e) {
            throw new IdInvalidException("Id không đúng định dạng UUID");
        }

        // Đảm bảo sắp xếp theo createdAt tăng dần (cũ nhất lên trước)
        Page<Message> page = chatMessageRepository
                .findAllByConversationConversationIdOrderByCreatedAtAsc(conversationIdUUID, pageable);

        return page.map(m -> toMessageResponse(m, viewerId));
    }
}