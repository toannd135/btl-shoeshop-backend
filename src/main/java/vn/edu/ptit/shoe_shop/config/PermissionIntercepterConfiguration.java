package vn.edu.ptit.shoe_shop.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class PermissionIntercepterConfiguration implements WebMvcConfigurer {
   @Bean
   PermissionInterceptor permissionIntercepter() {
       return new PermissionInterceptor();
   }

   @Override
   public void addInterceptors(InterceptorRegistry registry) {
       String[] whiteList = {
               "/",
               "/api/v1/auth/refresh-token",
               "/api/v1/auth/login",
               "/api/v1/auth/register",
               "/api/v1/auth/verify",
               "/api/v1/auth/forgot-password",
               "/api/v1/auth/verify-otp",
               "/api/v1/auth/reset-password",
               "/api/v1/recommend-products/**",
               "/storage/**",
               "/v3/api-docs/**",
               "/swagger-ui/**",
               "/swagger-ui.html"
       };

       registry.addInterceptor(permissionIntercepter())
               .addPathPatterns("/**")
               .excludePathPatterns(whiteList);
   }
}
