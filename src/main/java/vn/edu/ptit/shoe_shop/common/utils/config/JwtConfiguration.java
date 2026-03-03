    package vn.edu.ptit.shoe_shop.config;

    import com.nimbusds.jose.jwk.source.ImmutableSecret;
    import com.nimbusds.jose.util.Base64;
    import org.aspectj.weaver.loadtime.Agent;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.security.oauth2.jwt.JwtDecoder;
    import org.springframework.security.oauth2.jwt.JwtEncoder;
    import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
    import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
    import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
    import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
    import vn.edu.ptit.shoe_shop.common.constant.JwtConstants;

    import javax.crypto.SecretKey;
    import javax.crypto.spec.SecretKeySpec;



    @Configuration
    public class JwtConfiguration {

        @Value("${app.jwt.base64-secret}")
        private String jwtKey;

        @Bean
        public JwtEncoder jwtEncoder() {
            return new NimbusJwtEncoder(new ImmutableSecret<>(getSecretKey()));
        }

        private SecretKey getSecretKey() {
            byte[] keyBytes = Base64.from(jwtKey).decode();
            return new SecretKeySpec(keyBytes, 0, keyBytes.length, JwtConstants.Header.ALGORITHM.getName());
        }


        @Bean
        public JwtDecoder jwtDecoder() {
            NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey(getSecretKey())
                    .macAlgorithm(JwtConstants.Header.ALGORITHM).build();
            return token -> {
                try {
                    return jwtDecoder.decode(token);
                } catch (Exception e) {
                    throw e;
                }
            };
        }

        @Bean
        public JwtAuthenticationConverter jwtAuthenticationConverter() {
            JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
            grantedAuthoritiesConverter.setAuthorityPrefix("");
            grantedAuthoritiesConverter.setAuthoritiesClaimName("role");

            JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
            jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
            return jwtAuthenticationConverter;
        }


    }
