package vn.edu.ptit.shoe_shop.dto.response;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserShortInfo {
    private UUID userId;
    private String name;
    private String phone;
    private String email;
}