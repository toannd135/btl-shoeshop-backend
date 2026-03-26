package vn.edu.ptit.shoe_shop.repository;
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
    @EntityGraph(attributePaths = {"user", "admin"})
    List<Conversation> findByAdmin_UserIdOrderByUpdatedAtDesc(UUID adminId);
}
