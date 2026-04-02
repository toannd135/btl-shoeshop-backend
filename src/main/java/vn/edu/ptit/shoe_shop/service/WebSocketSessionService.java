package vn.edu.ptit.shoe_shop.service;

import java.util.UUID;

import vn.edu.ptit.shoe_shop.entity.WebSocketSession;

public interface WebSocketSessionService {
    public WebSocketSession createWebSocketSession(WebSocketSession webSocketSession);
    public void deleteSession(UUID sessionId);
}
