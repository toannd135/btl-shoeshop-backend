package vn.edu.ptit.shoe_shop.common.constant;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;

public final class JwtConstants {
    public static final class Header {
        public static final MacAlgorithm ALGORITHM = MacAlgorithm.HS512;
    }

    public static final class Claims {
        public static final String FIRST_NAME = "firstName";
        public static final String LAST_NAME = "lastName";
        public static final String ROLE = "role";
        public static final String USERNAME = "username";
        public static final String EMAIL = "email";
        public static final String TOKEN_TYPE = "tokenType";
        public static final String USER_PREFIX = "user:";
        public static final String SESSION_PREFIX = "session:";

    }

    public static final class Expiration {
        public static final long REMEMBER_ME_EXPIRATION_SECONDS = 604800L;
    }
}
