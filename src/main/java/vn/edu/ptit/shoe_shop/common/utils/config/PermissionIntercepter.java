package vn.edu.ptit.shoe_shop.config;

//
//@Component
//public class PermissionIntercepter implements HandlerInterceptor {
//    @Autowired
//    private UserService userService;
//
//    private static final Logger log = LoggerFactory.getLogger(PermissionIntercepter.class);
//
//    @Override
//    @Transactional
//    public boolean preHandle(HttpServletRequest request,
//                             HttpServletResponse response,
//                             Object handler) throws Exception {
//
//        String path = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
//        String httpMethod = request.getMethod();
//        log.debug("Checking permission for path: {}", path);
//        log.debug("HTTP Method: {}", httpMethod);
//        // check permission
//        String email = SecurityUtils.getCurrentUserLogin().isPresent() ? SecurityUtils.getCurrentUserLogin().get() : "";
//        if (email != null && !email.isEmpty()) {
//            log.debug("Authenticated user: {}", email);
//            User user = this.userService.getUserByUsernameOrEmail(email);
//            if (user != null) {
//                Role role = user.getRole();
//                if (role != null) {
//                    List<Permission> permissions = role.getPermissions();
//                    boolean isAllow = permissions.stream().anyMatch(p ->
//                            p.getApiPath().equals(path) && p.getMethod().equalsIgnoreCase(httpMethod));
//                    // neu khong co quyen
//                    if (!isAllow) {
//                        log.warn("Permission DENIED for user: {} | path: {} | method: {}", email, path, httpMethod);
//                        throw new AccessDeniedException("User don't have permission to access this resource");
//                    }
//                    log.info("Permission GRANTED for user: {} | path: {} | method: {}", email, path, httpMethod);
//                } else {
//                    log.error("User {} has no role assigned!", email);
//                    throw new AccessDeniedException("User don't have permission to access this resource");
//                }
//            }
//            else {
//                log.error("User not found in DB: {}", email);
//            }
//        } else {
//            log.warn("No authenticated user (email is empty)");
//        }
//        return true;
//    }
//}
