package vn.edu.ptit.shoe_shop.service.impl;

import vn.edu.ptit.shoe_shop.common.exception.IdInvalidException;
import vn.edu.ptit.shoe_shop.dto.response.Chat.ConversationItemResponse;
import vn.edu.ptit.shoe_shop.dto.response.Chat.ListConversationForAdminResponse;
import vn.edu.ptit.shoe_shop.dto.response.Chat.SenderSummary;
import vn.edu.ptit.shoe_shop.entity.Conversation;
import vn.edu.ptit.shoe_shop.entity.User;
import vn.edu.ptit.shoe_shop.repository.ConversationRepository;
import vn.edu.ptit.shoe_shop.repository.UserRepository;
import vn.edu.ptit.shoe_shop.service.ConversationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConversationServiceImpl implements ConversationService {
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;

    public static final String DEFAULT_ADMIN_ID = "6f83db48-ab38-4888-9d6d-4f61e3b451e5";

    @Transactional
    public Conversation addConversation(UUID userId) {
        return conversationRepository.findByUser_UserIdAndAdmin_UserId(userId, UUID.fromString(DEFAULT_ADMIN_ID))
                .orElseGet(() -> {
                    Conversation conversation = Conversation.builder()
                            .user(userRepository.findByUserId(userId).orElseThrow())
                            .admin(userRepository.findByUserId(UUID.fromString(DEFAULT_ADMIN_ID)).orElseThrow())
                            .build();
                    return conversationRepository.save(conversation);
                });
    }

    private SenderSummary toSenderSummary(User user) {
        return SenderSummary.builder()
                .senderId(user.getUserId().toString())
                .avatar(user.getAvatarImage())
                .senderName(user.getFirstName() + " " + user.getLastName())
                .build();
    }

    @Transactional(readOnly = true)
    public List<ConversationItemResponse> listConversationsForViewer(UUID viewerId) {
        boolean isAdminViewer = (viewerId.toString().equals(DEFAULT_ADMIN_ID));
        List<Conversation> conversations = isAdminViewer
                ? conversationRepository.findByAdmin_UserIdOrderByUpdatedAtDesc(viewerId)
                : conversationRepository.findByUser_UserIdOrderByUpdatedAtDesc(viewerId);

        return conversations.stream().map(c -> {
            User counterpart = isAdminViewer ? c.getUser() : c.getAdmin();
            return ConversationItemResponse.builder()
                    .conversationId(c.getConversationId().toString())
                    .senderSummary(toSenderSummary(counterpart))
                    .lastMessage(c.getLastMessage())
                    .updatedAt(c.getUpdatedAt())
                    .build();
        }).toList();
    }

    @Override
    public ListConversationForAdminResponse listConversationsForAdmin(UUID adminId) {
        List<Conversation> conversations = conversationRepository.findByAdmin_UserIdOrderByUpdatedAtDesc(adminId);
        return ListConversationForAdminResponse.builder()
                .conversations(conversations.stream().map(c -> {
                    User user = c.getUser();
                    return ConversationItemResponse.builder()
                            .conversationId(c.getConversationId().toString())
                            .senderSummary(toSenderSummary(user))
                            .lastMessage(c.getLastMessage())
                            .updatedAt(c.getUpdatedAt())
                            .build();
                }).toList())
                .build();
    }

}
