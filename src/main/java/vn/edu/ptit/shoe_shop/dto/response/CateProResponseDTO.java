package vn.edu.ptit.shoe_shop.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;


@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CateProResponseDTO {
    Integer productId;
    String title;
    LocalDateTime createdAt;
    String primaryImage;
}
