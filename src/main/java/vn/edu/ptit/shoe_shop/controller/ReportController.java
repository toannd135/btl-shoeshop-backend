package vn.edu.ptit.shoe_shop.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import vn.edu.ptit.shoe_shop.dto.response.RevenueReportDto;
import vn.edu.ptit.shoe_shop.dto.response.TopProductDto;
import vn.edu.ptit.shoe_shop.service.ReportService;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/reports")
@RequiredArgsConstructor
// @PreAuthorize("hasRole('ADMIN')") // Mở ra khi có bảo mật
public class ReportController {

    private final ReportService reportService;

    // 1. Thống kê doanh thu
    // GET
    // /api/admin/reports/revenue?startDate=2026-02-01T00:00:00Z&endDate=2026-02-28T23:59:59Z
    @GetMapping("/revenue")
    public ResponseEntity<List<RevenueReportDto>> getRevenueReport(
            @RequestParam(required = false) Instant startDate,
            @RequestParam(required = false) Instant endDate) {
        return ResponseEntity.ok(reportService.getRevenueReport(startDate, endDate));
    }

    // 2. Thống kê Top Sản phẩm
    // GET /api/admin/reports/top-products?limitProduct=10
    @GetMapping("/top-products")
    public ResponseEntity<List<TopProductDto>> getTopProducts(
            @RequestParam(required = false) Instant startDate,
            @RequestParam(required = false) Instant endDate,
            @RequestParam(defaultValue = "5") int limitProduct) { // Mặc định lấy Top 5
            PageRequest pageRequest = PageRequest.of(0,limitProduct);
        return ResponseEntity.ok(reportService.getTopProducts(startDate, endDate, pageRequest));
    }
}