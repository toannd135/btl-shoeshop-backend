package vn.edu.ptit.shoe_shop.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import vn.edu.ptit.shoe_shop.dto.response.ApiResponse;

import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    private final ObjectMapper mapper;
    private final AccessDeniedHandler delegate = new BearerTokenAccessDeniedHandler();
    public CustomAccessDeniedHandler(ObjectMapper mapper) {
        this.mapper = mapper;
    }
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
            throws IOException, ServletException {
        delegate.handle(request, response, accessDeniedException);
        response.setContentType("application/json;charset=UTF-8");
        ApiResponse<Object> res = new ApiResponse<>();
        res.setStatusCode(HttpStatus.FORBIDDEN.value());
        res.setError(
                accessDeniedException.getCause() != null
                        ? accessDeniedException.getCause().getMessage()
                        : accessDeniedException.getMessage()
        );
        res.setMessage("Access Denied...........");
        mapper.writeValue(response.getWriter(), res);
    }
}
