package vn.edu.ptit.shoe_shop.common.exception;

public class TokenExpiredOrUsedException extends RuntimeException {
    public TokenExpiredOrUsedException(String message) {
        super(message);
    }
}
