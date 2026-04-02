package vn.edu.ptit.shoe_shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.edu.ptit.shoe_shop.entity.Message;

import java.util.List;
import java.util.UUID;
@Repository
public interface ChatMessageRepository extends JpaRepository<Message, UUID> {
    List<Message> findAllByConversationConversationIdOrderByCreatedAtAsc(UUID conversationId);
}
