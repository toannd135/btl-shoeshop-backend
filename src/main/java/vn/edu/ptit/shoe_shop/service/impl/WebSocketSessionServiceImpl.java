package vn.edu.ptit.shoe_shop.service.impl;

import vn.edu.ptit.shoe_shop.service.WebSocketSessionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import vn.edu.ptit.shoe_shop.entity.WebSocketSession;
import vn.edu.ptit.shoe_shop.repository.WebSocketSessionRepository;

import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WebSocketSessionServiceImpl implements WebSocketSessionService {
    WebSocketSessionRepository webSocketSessionRepository;

    public WebSocketSession createWebSocketSession(WebSocketSession webSocketSession) {
        return webSocketSessionRepository.save(webSocketSession);
    }

    public void deleteSession(String sessionId) {
        webSocketSessionRepository.deleteBySocketSessionId(sessionId);
    }
}
