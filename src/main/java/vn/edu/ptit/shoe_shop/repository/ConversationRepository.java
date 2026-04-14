package vn.edu.ptit.shoe_shop.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import vn.edu.ptit.shoe_shop.entity.Conversation;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, UUID> {
    Optional<Conversation> findByUser_UserIdAndAdmin_UserId(UUID user_id, UUID admin_id);

    List<Conversation> findByUser_UserIdOrderByUpdatedAtDesc(UUID userId);

    // lấy các conversation của viewer là ADMIN
    @EntityGraph(attributePaths = { "user", "admin" })
    List<Conversation> findByAdmin_UserIdOrderByUpdatedAtDesc(UUID adminId);

    /*
     * Cập nhật nhanh lastMessage và updatedAt của Conversation
     * mà không cần load entity lên Hibernate.
     */
            @Modifying
            @Query("""
            UPDATE Conversation c
            SET c.lastMessage = :content,
                c.updatedAt = CURRENT_TIMESTAMP
            WHERE c.conversationId = :id
            """)
            void updateLastMessage(UUID id, String content);

            /**
             * Đánh dấu admin đã đọc một conversation.
             */
            @Modifying
            @Query("UPDATE Conversation c SET c.isReadByAdmin = true WHERE c.conversationId = :id")
            void markAsReadByAdmin(UUID id);

            /**
             * Đánh dấu admin chưa đọc (khi có tin nhắn mới từ user gửi vào).
             */
            @Modifying
            @Query("UPDATE Conversation c SET c.isReadByAdmin = false WHERE c.conversationId = :id")
            void markAsUnreadByAdmin(UUID id);
        
}