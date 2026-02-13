package vn.edu.ptit.shoe_shop.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.ptit.shoe_shop.constant.StatusEnum;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductVariantImageCreateRequestDTO {
    @NotNull
    MultipartFile image;

    Boolean isPrimary;

    StatusEnum status;
}
