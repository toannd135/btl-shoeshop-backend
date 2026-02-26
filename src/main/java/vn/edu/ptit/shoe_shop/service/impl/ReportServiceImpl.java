package vn.edu.ptit.shoe_shop.service.impl;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import vn.edu.ptit.shoe_shop.dto.response.RevenueReportDto;
import vn.edu.ptit.shoe_shop.dto.response.TopProductDto;
import vn.edu.ptit.shoe_shop.repository.ReportRepository;
import vn.edu.ptit.shoe_shop.service.ReportService;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;

    // Lấy doanh thu trong khoảng thời gian
    public List<RevenueReportDto> getRevenueReport(Instant startDate, Instant endDate) {
        // Nếu không truyền ngày, mặc định lấy 30 ngày gần nhất
        if (startDate == null || endDate == null) {
            endDate = Instant.now();
            startDate = endDate.minus(30, ChronoUnit.DAYS);
        }
        return reportRepository.getDailyRevenue(startDate, endDate);
    }

    // Lấy Top sản phẩm bán chạy
    public List<TopProductDto> getTopProducts(Instant startDate, Instant endDate, Pageable pageable) {
        if (startDate == null || endDate == null) {
            endDate = Instant.now();
            startDate = endDate.minus(30, ChronoUnit.DAYS); // Mặc định top 30 ngày
        }
        return reportRepository.getTopSellingProducts(startDate, endDate, pageable);
    }

}
