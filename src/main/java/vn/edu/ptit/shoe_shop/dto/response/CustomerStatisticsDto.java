package vn.edu.ptit.shoe_shop.dto.response;

// 3. Thống kê tổng quan khách hàng
public interface CustomerStatisticsDto {
    Integer getTotalCustomers();
    Integer getNewCustomersThisMonth();
    Integer getCustomersWithOrders(); // Số khách hàng đã từng mua ít nhất 1 lần
}