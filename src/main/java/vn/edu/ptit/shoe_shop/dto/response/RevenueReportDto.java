package vn.edu.ptit.shoe_shop.dto.response;

import java.math.BigDecimal;

// Thống kê doanh thu theo ngày/tháng
public interface RevenueReportDto {
    String getReportDate(); // Trả về ngày hoặc tháng (YYYY-MM-DD)
    BigDecimal getTotalRevenue(); // Tổng tiền
    Integer getTotalOrders(); // Tổng số đơn thành công
}