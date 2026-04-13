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
import vn.edu.ptit.shoe_shop.repository.UserRepository;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;


@Component
public class PermissionInterceptor implements HandlerInterceptor {
   @Autowired
   private UserRepository userRepository;

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
       UUID userid = SecurityUtils.getCurrentUserId();

       if (userid != null) {
           User user = this.userRepository.findByUserId(userid).orElse(null);
           if (user != null) {
               Role role = user.getRole();
               if (role != null) {
                   List<Permission> permissions = role.getPermissions();
                   boolean isAllow = permissions.stream().anyMatch(p ->
                           p.getApiPath().equals(path)
                                   && p.getMethod().equalsIgnoreCase(httpMethod)
                   );
                   if (!isAllow) {
                       log.warn("Permission DENIED for userId: {} | path: {} | method: {}",
                               userid, path, httpMethod);
                       throw new AccessDeniedException("User don't have permission to access this resource");
                   }
                   log.info("Permission GRANTED for userId: {} | path: {} | method: {}",
                           userid, path, httpMethod);
               } else {
                   log.error("UserId {} has no role assigned!", userid);
                   throw new AccessDeniedException("User don't have permission to access this resource");
               }

           } else {
               log.warn("User not found in DB for userId: {}", userid);
           }

       } else {
           log.debug("No authenticated user or anonymous user");
       }
       return true;
   }
}
