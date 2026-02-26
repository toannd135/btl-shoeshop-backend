package vn.edu.ptit.shoe_shop.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import vn.edu.ptit.shoe_shop.dto.response.ApiResponse;
import vn.edu.ptit.shoe_shop.service.RedisService;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final RedisService redisService;
    private final ObjectMapper objectMapper;

    public JwtAuthenticationFilter(RedisService redisService, ObjectMapper objectMapper) {
        this.redisService = redisService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String accessToken = getJwtFromRequest(request);
        if(StringUtils.hasText(accessToken)) {
            boolean isBlacklisted = this.redisService.isBlacklisted(accessToken);
            if(isBlacklisted) {
                ApiResponse<Object> apiResponse = new ApiResponse<>();
                apiResponse.setStatusCode(HttpStatus.UNAUTHORIZED.value());
                apiResponse.setError("Unauthorized");
                apiResponse.setMessage("Token has been revoked. Please login again.");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                String json = objectMapper.writeValueAsString(apiResponse);
                response.getWriter().write(json);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
