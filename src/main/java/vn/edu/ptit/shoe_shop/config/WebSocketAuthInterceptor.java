package vn.edu.ptit.shoe_shop.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketAuthInterceptor implements HandshakeInterceptor {
    private final JwtDecoder jwtDecoder;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        log.info("WebSocket handshake request from: {}", request.getRemoteAddress());

        String authHeader = null;

        // Try to get from headers
        if (request.getHeaders().containsKey("Authorization")) {
            List<String> authHeaders = request.getHeaders().get("Authorization");
            if (authHeaders != null && !authHeaders.isEmpty()) {
                authHeader = authHeaders.get(0);
            }
        }

        // Try to get from query params (for SockJS)
        if (authHeader == null && request instanceof ServletServerHttpRequest servletRequest) {
            String query = servletRequest.getServletRequest().getQueryString();
            if (query != null && query.contains("Authorization=")) {
                for (String param : query.split("&")) {
                    if (param.startsWith("Authorization=")) {
                        authHeader = URLDecoder.decode(
                                param.substring("Authorization=".length()),
                                StandardCharsets.UTF_8);
                        break;
                    }
                }
            }
        }

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                Jwt jwt = jwtDecoder.decode(token);
                log.info("JWT validated for user: {}", jwt.getSubject());

                // Create authentication with JWT as principal
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(jwt, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
                SecurityContextHolder.getContext().setAuthentication(authentication);

                attributes.put("jwt", jwt);
                return true;
            } catch (JwtException e) {
                log.warn("Invalid JWT token in WebSocket handshake: {}", e.getMessage());
                // Allow anonymous connection for guest users
                return true;
            }
        }

        // Allow anonymous connection
        log.info("No JWT token found, allowing anonymous WebSocket connection");
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                              WebSocketHandler wsHandler, Exception exception) {
        // No action needed after handshake
    }
}
