package vn.edu.ptit.shoe_shop.dto.response;

import java.math.BigDecimal;


//  Thống kê khách VIP (Dùng Interface Projection để hứng dữ liệu từ DB)
public interface TopCustomerDto {
    String getUserId();
    String getFullName();
    String getEmail();
    String getPhone();
    Integer getTotalOrders();     // Tổng số đơn đã mua thành công
    BigDecimal getTotalSpent();   // Tổng tiền đã chi tiêu
}