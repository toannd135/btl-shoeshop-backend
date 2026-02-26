package vn.edu.ptit.shoe_shop.service;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Pageable;

import vn.edu.ptit.shoe_shop.dto.response.RevenueReportDto;
import vn.edu.ptit.shoe_shop.dto.response.TopProductDto;

public interface ReportService {
    public List<RevenueReportDto> getRevenueReport(Instant startDate, Instant endDate);

    public List<TopProductDto> getTopProducts(Instant startDate, Instant endDate, Pageable pageable);
}
