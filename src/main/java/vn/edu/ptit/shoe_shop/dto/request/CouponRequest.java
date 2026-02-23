package vn.edu.ptit.shoe_shop.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import vn.edu.ptit.shoe_shop.constant.enums.DiscountTypeEnum;
import vn.edu.ptit.shoe_shop.constant.enums.StatusEnum;

import java.math.BigDecimal;
import java.time.Instant;



@Getter
@Setter
public class CouponRequest {
    @NotBlank(message = "Mã giảm giá không được để trống")
    @Size(min = 4, max = 50, message = "Mã giảm giá phải từ 4 đến 50 ký tự")
    @Pattern(regexp = "^[A-Z0-9]+$", message = "Mã giảm giá chỉ được chứa chữ cái in hoa và số, không có khoảng trắng")
    private String code;

    @NotBlank(message = "Tên chương trình khuyến mãi không được để trống")
    @Size(max = 255, message = "Tên không được vượt quá 255 ký tự")
    private String name;

    @NotNull(message = "Loại giảm giá không được để trống")
    private DiscountTypeEnum discountType;

    @NotNull(message = "Giá trị giảm không được để trống")
    @Positive(message = "Giá trị giảm phải lớn hơn 0")
    private BigDecimal discountValue;

    @NotNull(message = "Giá trị đơn tối thiểu không được để trống")
    @Min(value = 0, message = "Giá trị đơn tối thiểu không được là số âm")
    private BigDecimal minOrderValue;

    @Min(value = 0, message = "Giá trị giảm tối đa không được là số âm")
    private BigDecimal maxDiscount;

    @NotNull(message = "Giới hạn số lượt sử dụng không được để trống")
    @Min(value = 1, message = "Giới hạn sử dụng phải ít nhất là 1")
    private Integer usageLimit;

    @NotNull(message = "Thời gian bắt đầu không được để trống")
    @FutureOrPresent(message = "Thời gian bắt đầu không được nằm trong quá khứ")
    private Instant startsAt;

    @NotNull(message = "Thời gian kết thúc không được để trống")
    @Future(message = "Thời gian kết thúc phải nằm trong tương lai")
    private Instant expiresAt;

    @NotNull(message = "Trạng thái không được để trống")
    private StatusEnum status;
}