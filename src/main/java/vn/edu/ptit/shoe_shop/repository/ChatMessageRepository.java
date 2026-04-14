package vn.edu.ptit.shoe_shop.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.edu.ptit.shoe_shop.entity.Message;

import java.util.UUID;
@Repository
public interface ChatMessageRepository extends JpaRepository<Message, UUID> {

    Page<Message> findByConversationConversationId(UUID conversationId, Pageable pageable);
    // Page<Message> findByConversationConversationIdOrderByCreatedAtDesc(UUID conversationId);
}