package vn.edu.ptit.shoe_shop.common.utils.request;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class RequestUtils {

    public static String getClientIp(HttpServletRequest request) {
        if (request == null) return "unknown";
        String remoteAddr = request.getHeader("X-Forwarded-For");
        if (remoteAddr == null || remoteAddr.isEmpty() || "unknown".equalsIgnoreCase(remoteAddr)) {
            remoteAddr = request.getRemoteAddr();
        }
        if (remoteAddr != null && remoteAddr.contains(",")) {
            return remoteAddr.split(",")[0].trim();
        }
        return remoteAddr;
    }
}