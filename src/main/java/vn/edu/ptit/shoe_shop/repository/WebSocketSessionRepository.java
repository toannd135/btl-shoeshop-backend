package vn.edu.ptit.shoe_shop.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.edu.ptit.shoe_shop.entity.WebSocketSession;

@Repository
public interface WebSocketSessionRepository extends JpaRepository<WebSocketSession, String> {
    void deleteBySocketSessionId(String sessionId);
}
