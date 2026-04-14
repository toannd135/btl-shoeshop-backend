package vn.edu.ptit.shoe_shop.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import vn.edu.ptit.shoe_shop.dto.response.RevenueReportDto;
import vn.edu.ptit.shoe_shop.dto.response.TopProductDto;
import vn.edu.ptit.shoe_shop.service.ReportExportService;
import vn.edu.ptit.shoe_shop.service.ReportService;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
// @PreAuthorize("hasRole('ADMIN')") // Mở ra khi có bảo mật
public class    ReportController {

    private final ReportService reportService;
    private final ReportExportService reportExportService;
    // 1. Thống kê doanh thu
    // GET
    // /api/admin/reports/revenue?startDate=2026-02-01T00:00:00Z&endDate=2026-02-28T23:59:59Z
    @GetMapping("/reports/revenue")
    public ResponseEntity<List<RevenueReportDto>> getRevenueReport(
            @RequestParam(required = false) Instant startDate,
            @RequestParam(required = false) Instant endDate) {
        return ResponseEntity.ok(reportService.getRevenueReport(startDate, endDate));
    }

    // 2. Thống kê Top Sản phẩm
    // GET /api/admin/reports/top-products?limitProduct=10
    @GetMapping("/analytics/top-selling-products")
    public ResponseEntity<List<TopProductDto>> getTopProducts(
            @RequestParam(required = false) Instant startDate,
            @RequestParam(required = false) Instant endDate,
            @RequestParam(defaultValue = "5") int limitProduct) { // Mặc định lấy Top 5
            PageRequest pageRequest = PageRequest.of(0,limitProduct);
        return ResponseEntity.ok(reportService.getTopProducts(startDate, endDate, pageRequest));
    }

    @GetMapping("/reports/export/excel")
    public void exportExcel(
            @RequestParam(required = false) Instant startDate,
            @RequestParam(required = false) Instant endDate,
            @RequestParam(defaultValue = "5") int limitProduct,
            @RequestParam(defaultValue = "5") int limitCustomer,
            HttpServletResponse response) throws IOException {

        byte[] excelData = reportExportService.exportAllReportsToExcel(startDate, endDate, limitProduct, limitCustomer);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=thong_ke_tong_hop.xlsx");
        response.setContentLength(excelData.length);
        response.getOutputStream().write(excelData);
        response.flushBuffer();
    }

    // 4. Xuất báo cáo thống kê ra CSV
    // GET /api/v1/admin/reports/export/csv?startDate=...&endDate=...&limitProduct=5&limitCustomer=5
    @GetMapping("/reports/export/csv")
    public void exportCsv(
            @RequestParam(required = false) Instant startDate,
            @RequestParam(required = false) Instant endDate,
            @RequestParam(defaultValue = "5") int limitProduct,
            @RequestParam(defaultValue = "5") int limitCustomer,
            HttpServletResponse response) throws IOException {

        byte[] csvData = reportExportService.exportAllReportsToCsv(startDate, endDate, limitProduct, limitCustomer);

        response.setContentType("text/csv; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=thong_ke_tong_hop.csv");
        response.setContentLength(csvData.length);
        response.getOutputStream().write(csvData);
        response.flushBuffer();
    }
}