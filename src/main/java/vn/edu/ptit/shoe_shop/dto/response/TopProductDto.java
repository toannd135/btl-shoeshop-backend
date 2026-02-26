package vn.edu.ptit.shoe_shop.dto.response;

import java.math.BigDecimal;

//  Thống kê sản phẩm bán chạy
public interface TopProductDto {
    String getProductId();
    String getProductName();
    Integer getTotalSold(); // Tổng số lượng đã bán
    BigDecimal getTotalRevenue(); // Doanh thu mang lại từ sản phẩm này
}