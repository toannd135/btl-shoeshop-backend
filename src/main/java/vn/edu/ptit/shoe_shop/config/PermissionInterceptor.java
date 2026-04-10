package vn.edu.ptit.shoe_shop.config;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import vn.edu.ptit.shoe_shop.common.security.SecurityUtils;
import vn.edu.ptit.shoe_shop.entity.Permission;
import vn.edu.ptit.shoe_shop.entity.Role;
import vn.edu.ptit.shoe_shop.entity.User;
import vn.edu.ptit.shoe_shop.service.UserService;

import java.nio.file.AccessDeniedException;
import java.util.List;

@Component
public class PermissionInterceptor implements HandlerInterceptor {
   @Autowired
   private UserService userService;

   private static final Logger log = LoggerFactory.getLogger(PermissionInterceptor.class);

   @Override
   @Transactional
   public boolean preHandle(HttpServletRequest request,
                            HttpServletResponse response,
                            Object handler) throws Exception {

       String path = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
       String httpMethod = request.getMethod();
       log.debug("Checking permission for path: {}", path);
       log.debug("HTTP Method: {}", httpMethod);
       // check permission
       String email = SecurityUtils.getCurrentUserLogin().orElse("");
       if (email != null && !email.isEmpty() && !email.equals("anonymousUser")) {
           log.debug("Authenticated user: {}", email);
           User user = null;
           try {
               user = this.userService.getUserByUsernameOrEmail(email);
           } catch (Exception e) {
               log.error("User not found in DB: {}", email);
           }

           if (user != null) {
               Role role = user.getRole();
               if (role != null) {
                   List<Permission> permissions = role.getPermissions();
                   boolean isAllow = permissions.stream().anyMatch(p ->
                           p.getApiPath().equals(path) && p.getMethod().equalsIgnoreCase(httpMethod));
                   // neu khong co quyen
                   if (!isAllow) {
                       log.warn("Permission DENIED for user: {} | path: {} | method: {}", email, path, httpMethod);
                       throw new AccessDeniedException("User don't have permission to access this resource");
                   }
                   log.info("Permission GRANTED for user: {} | path: {} | method: {}", email, path, httpMethod);
               } else {
                   log.error("User {} has no role assigned!", email);
                   throw new AccessDeniedException("User don't have permission to access this resource");
               }
           }
           else {
               // User not found in DB but email was present.
               // This can happen if the token is valid but the user was deleted or it's a public endpoint with an old token.
               log.warn("User not found in DB for email: {}. Allowing request if it's a public endpoint or will be blocked by Security.", email);
           }
       } else {
           log.debug("No authenticated user or anonymous user");
       }
       return true;
   }
}
