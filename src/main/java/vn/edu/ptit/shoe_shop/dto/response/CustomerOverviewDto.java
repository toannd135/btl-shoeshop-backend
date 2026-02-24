package vn.edu.ptit.shoe_shop.dto.response;
import lombok.Builder;
import lombok.Data;


//  Thống kê tổng quan (Class thông thường vì ta sẽ gom dữ liệu từ nhiều câu query)
@Data
@Builder
public class CustomerOverviewDto {
    private long totalCustomers;          // Tổng số tài khoản khách hàng
    private long newCustomersThisMonth;   // Số khách hàng mới đăng ký trong tháng
    private long customersWithOrders;     // Số khách hàng đã từng mua ít nhất 1 đơn
}