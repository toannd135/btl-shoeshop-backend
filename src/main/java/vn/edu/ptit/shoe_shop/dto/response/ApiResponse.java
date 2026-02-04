package vn.edu.ptit.shoe_shop.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private int statusCode;
    private String error;
    private Object message;
    private T data;

    public static <T> ApiResponse<T> ok(T data) {
        return ApiResponse.<T>builder()
                .statusCode(1000)
                .message("Success")
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> ok(T data, String msg) {
        return ApiResponse.<T>builder()
                .statusCode(1000)
                .message(msg)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> error(int code, String msg) {
        return ApiResponse.<T>builder()
                .statusCode(code)
                .message(msg)
                .build();
    }
}
