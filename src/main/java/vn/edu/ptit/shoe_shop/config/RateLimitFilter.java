package vn.edu.ptit.shoe_shop.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import vn.edu.ptit.shoe_shop.dto.response.ApiResponse;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Order(1)
public class RateLimitFilter extends OncePerRequestFilter {
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;

    public RateLimitFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();
        if("POST".equals(request.getMethod())) {
            if ("/api/v1/auth/login".equals(uri)) {
                handleRateLimit(request, response, filterChain, "login");
            } else if ("/api/v1/auth/register".equals(uri)) {
                handleRateLimit(request, response, filterChain, "register");
            } else if ("/api/v1/auth/forgot-password".equals(uri)) {
                handleRateLimit(request, response, filterChain, "forgot-password");
            } else {
                filterChain.doFilter(request, response);
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }

    private void handleRateLimit(HttpServletRequest request,
                                 HttpServletResponse response,
                                 FilterChain filterChain,
                                 String type) throws ServletException, IOException {
        String ip = getCurrentClientIp(request);
        // nếu cho token trong xô rồi thì lấy xô ra. nếu không thì tạo 1 xô mới
        Bucket bucket = buckets.computeIfAbsent(ip, key -> switch (type) {
            case "register" -> createRegisterBuilder();
            case "forgot-password" -> createForgotPasswordBuilder();
            default -> createLoginBuilder();
        });
        if(bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            ApiResponse<Object> apiResponse = new ApiResponse<>();
            apiResponse.setStatusCode(HttpStatus.TOO_MANY_REQUESTS.value());
            apiResponse.setError("Too Many Requests");
            apiResponse.setMessage("Too many failed login attempts.");
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            String json = this.objectMapper.writeValueAsString(apiResponse);
            response.getWriter().write(json);
        }
    }

    private Bucket createLoginBuilder() {
        Bandwidth limit = Bandwidth
                .builder()
                .capacity(5)
                .refillIntervally(5, Duration.ofMinutes(5))
                .build();
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }


    private Bucket createRegisterBuilder() {
        Bandwidth limit = Bandwidth
                .builder()
                .capacity(3)
                .refillIntervally(3, Duration.ofMinutes(30))
                .build();
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    private Bucket createForgotPasswordBuilder() {
        Bandwidth limit = Bandwidth
                .builder()
                .capacity(2)
                .refillIntervally(2, Duration.ofMinutes(15))
                .build();
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    private String getCurrentClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if(xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0].trim();
    }
}
