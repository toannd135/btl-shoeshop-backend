package vn.edu.ptit.shoe_shop.service.impl;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vn.edu.ptit.shoe_shop.dto.response.CustomerOverviewDto;
import vn.edu.ptit.shoe_shop.dto.response.TopCustomerDto;
import vn.edu.ptit.shoe_shop.repository.UserRepository;
import vn.edu.ptit.shoe_shop.service.CustomerReportService;

@Service
@RequiredArgsConstructor
public class CustomerReportServiceImpl implements CustomerReportService{
    private final UserRepository userRepository;
    // 1. Lấy dữ liệu tổng quan khách hàng
    public CustomerOverviewDto getCustomerOverview() {
        // Mốc thời gian: 30 ngày trước (hoặc tính từ ngày 1 của tháng hiện tại tùy nghiệp vụ)
        Instant thirtyDaysAgo = Instant.now().minus(30, ChronoUnit.DAYS);

        long total = userRepository.count(); // Nếu muốn loại trừ Admin, dùng countByRole(...)
        long newCustomers = userRepository.countByCreatedAtAfter(thirtyDaysAgo);
        long activeCustomers = userRepository.countCustomersWithSuccessfulOrders();

        return CustomerOverviewDto.builder()
                .totalCustomers(total)
                .newCustomersThisMonth(newCustomers)
                .customersWithOrders(activeCustomers)
                .build();
    }

    // 2. Lấy danh sách Khách VIP
    public List<TopCustomerDto> getTopSpenders(int limit) {
        // Đảm bảo limit không quá lớn gây chậm DB
        if (limit <= 0 || limit > 100) {
            limit = 10;
        }
        return userRepository.getTopSpendingCustomers(limit);
    }
}
