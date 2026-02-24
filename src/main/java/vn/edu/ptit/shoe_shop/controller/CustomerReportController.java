package vn.edu.ptit.shoe_shop.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import vn.edu.ptit.shoe_shop.dto.response.CustomerOverviewDto;
import vn.edu.ptit.shoe_shop.dto.response.TopCustomerDto;
import vn.edu.ptit.shoe_shop.service.CustomerReportService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/reports/customers")
@RequiredArgsConstructor
// @PreAuthorize("hasRole('ADMIN')")
public class CustomerReportController {

    private final CustomerReportService customerReportService;

    // 1. Lấy tổng quan thống kê khách hàng
    // GET /api/admin/reports/customers/overview
    @GetMapping("/overview")
    public ResponseEntity<CustomerOverviewDto> getOverview() {
        return ResponseEntity.ok(customerReportService.getCustomerOverview());
    }

    // 2. Lấy danh sách khách hàng chi tiêu nhiều nhất (VIP)
    // GET /api/admin/reports/customers/top-spenders?limit=10
    @GetMapping("/top-spenders")
    public ResponseEntity<List<TopCustomerDto>> getTopSpenders(
            @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(customerReportService.getTopSpenders(limit));
    }
}