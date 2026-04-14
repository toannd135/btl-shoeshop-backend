package vn.edu.ptit.shoe_shop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class PermissionInterceptorConfiguration implements WebMvcConfigurer {
    @Bean
    PermissionInterceptor permissionInterceptor() {
        return new PermissionInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        String[] whiteList = {
                "/",
                "/api/v2/users/me/**",
                "/api/v1/users/me/**",
                "/api/v1/products/**",
                "/api/v1/cart/**",
                "/api/v1/cloudinary/**",
                "/api/v1/file/**",
                "/api/v1/categories/**",
                "/api/v1/orders/**",
                "/api/v1/coupons/**",
                "/api/v1/variants/**",
                "/api/v1/brands/**",
                "/api/v1/recommend-products/**",
                "/api/v1/auth/refresh-token",
                "/api/v1/auth/login",
                "/api/v1/auth/logout",
                "/api/v1/auth/register",
                "/api/v1/auth/verify",
                "/api/v1/auth/forgot-password",
                "/api/v1/auth/verify-otp",
                "/api/v1/auth/reset-password",
                "/api/v1/auth/oauth2-success",
                "/api/v1/recommend-products/**",
                "/storage/**",
                "/v3/api-docs/**",
                "/swagger-ui/**",
                "/swagger-ui.html",
                "/ws/**",
                "/ws",
                "/api/v1/chat/**",
                "/api/v1/conversations/**"
        };

        registry.addInterceptor(permissionInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(whiteList);
    }
}
