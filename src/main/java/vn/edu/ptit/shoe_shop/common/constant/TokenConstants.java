package vn.edu.ptit.shoe_shop.common.constant;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;

public class TokenConstants {
    public static final String ACCESS_TOKEN = "accessToken";
    public static final String REFRESH_TOKEN = "refreshToken";
    public static final String FAKE_TOKEN = "fakeToken";
//    redis key prefix
    public static final String ACCESS_PREFIX = "ACCESS_TOKEN";
    public static final String REFRESH_PREFIX = "REFRESH_TOKEN";
    public static final String RESET_PREFIX = "RESET_TOKEN";
    public static final String OTP_PREFIX = "OTP_TOKEN";
    public static final String VERIFY_PREFIX = "VERIFY_TOKEN";
    public static final String BLACKLIST_PREFIX = "BLACKLIST_TOKEN";
}
