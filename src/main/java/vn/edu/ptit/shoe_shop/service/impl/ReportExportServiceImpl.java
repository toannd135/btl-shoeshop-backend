package vn.edu.ptit.shoe_shop.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import vn.edu.ptit.shoe_shop.dto.response.CustomerOverviewDto;
import vn.edu.ptit.shoe_shop.dto.response.RevenueReportDto;
import vn.edu.ptit.shoe_shop.dto.response.TopCustomerDto;
import vn.edu.ptit.shoe_shop.dto.response.TopProductDto;
import vn.edu.ptit.shoe_shop.service.CustomerReportService;
import vn.edu.ptit.shoe_shop.service.ReportExportService;
import vn.edu.ptit.shoe_shop.service.ReportService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportExportServiceImpl implements ReportExportService {

    private final ReportService reportService;
    private final CustomerReportService customerReportService;

    public byte[] exportAllReportsToExcel(Instant startDate, Instant endDate,
                                          int limitProduct, int limitCustomer) throws IOException {
        List<RevenueReportDto> revenueList = reportService.getRevenueReport(startDate, endDate);
        List<TopProductDto> topProducts = reportService.getTopProducts(startDate, endDate, PageRequest.of(0, limitProduct));
        List<TopCustomerDto> topCustomers = customerReportService.getTopSpenders(limitCustomer);
        CustomerOverviewDto overview = customerReportService.getCustomerOverview();

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            createRevenueSheet(workbook, revenueList, startDate, endDate);
            createTopProductSheet(workbook, topProducts);
            createTopCustomerSheet(workbook, topCustomers);
            createOverviewSheet(workbook, overview);

            workbook.write(out);
            return out.toByteArray();
        }
    }

    private void createRevenueSheet(Workbook workbook, List<RevenueReportDto> revenueList,
                                    Instant startDate, Instant endDate) {
        Sheet sheet = workbook.createSheet("Doanh thu");
        int rowNum = 0;

        // Định dạng ngày dd/MM/yyyy theo UTC (có thể đổi zone nếu cần)
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy").withZone(ZoneOffset.UTC);
        String startStr = startDate != null ? dateFormatter.format(startDate) : "Không giới hạn";
        String endStr = endDate != null ? dateFormatter.format(endDate) : "Không giới hạn";

        // Tạo style cho số tiền (nếu muốn)
        CellStyle currencyStyle = workbook.createCellStyle();
        currencyStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0.00"));
        CellStyle integerStyle = workbook.createCellStyle();
        integerStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0"));

        Row header = sheet.createRow(rowNum++);
        header.createCell(0).setCellValue("Thời gian (ngày/tháng)");
        header.createCell(1).setCellValue("Tổng doanh thu");
        header.createCell(2).setCellValue("Số đơn hàng");

        for (RevenueReportDto dto : revenueList) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(dto.getReportDate());
            Cell revenueCell = row.createCell(1);
            revenueCell.setCellValue(dto.getTotalRevenue().doubleValue());
            revenueCell.setCellStyle(currencyStyle);
            Cell orderCell = row.createCell(2);
            orderCell.setCellValue(dto.getTotalOrders());
            orderCell.setCellStyle(integerStyle);
        }

        Row filterRow = sheet.createRow(rowNum + 1);
        filterRow.createCell(0).setCellValue("Ngày bắt đầu: " + startStr);
        filterRow.createCell(1).setCellValue("Ngày kết thúc: " + endStr);

        for (int i = 0; i < 3; i++) sheet.autoSizeColumn(i);
    }

    private void createTopProductSheet(Workbook workbook, List<TopProductDto> topProducts) {
        Sheet sheet = workbook.createSheet("Top sản phẩm bán chạy");
        int rowNum = 0;

        CellStyle integerStyle = workbook.createCellStyle();
        integerStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0"));
        CellStyle currencyStyle = workbook.createCellStyle();
        currencyStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0.00"));

        Row header = sheet.createRow(rowNum++);
        header.createCell(0).setCellValue("Mã sản phẩm");
        header.createCell(1).setCellValue("Tên sản phẩm");
        header.createCell(2).setCellValue("Số lượng bán");
        header.createCell(3).setCellValue("Doanh thu");

        for (TopProductDto dto : topProducts) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(dto.getProductId());
            row.createCell(1).setCellValue(dto.getProductName());
            Cell qtyCell = row.createCell(2);
            qtyCell.setCellValue(dto.getTotalSold());
            qtyCell.setCellStyle(integerStyle);
            Cell revCell = row.createCell(3);
            revCell.setCellValue(dto.getTotalRevenue().doubleValue());
            revCell.setCellStyle(currencyStyle);
        }

        for (int i = 0; i < 4; i++) sheet.autoSizeColumn(i);
    }

    private void createTopCustomerSheet(Workbook workbook, List<TopCustomerDto> topCustomers) {
        Sheet sheet = workbook.createSheet("Top khách hàng VIP");
        int rowNum = 0;

        CellStyle integerStyle = workbook.createCellStyle();
        integerStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0"));
        CellStyle currencyStyle = workbook.createCellStyle();
        currencyStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0.00"));

        Row header = sheet.createRow(rowNum++);
        header.createCell(0).setCellValue("Mã khách hàng");
        header.createCell(1).setCellValue("Họ tên");
        header.createCell(2).setCellValue("Email");
        header.createCell(3).setCellValue("Số điện thoại");
        header.createCell(4).setCellValue("Tổng đơn hàng");
        header.createCell(5).setCellValue("Tổng chi tiêu");

        for (TopCustomerDto dto : topCustomers) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(dto.getUserId());
            row.createCell(1).setCellValue(dto.getFullName());
            row.createCell(2).setCellValue(dto.getEmail());
            row.createCell(3).setCellValue(dto.getPhone());
            Cell orderCell = row.createCell(4);
            orderCell.setCellValue(dto.getTotalOrders());
            orderCell.setCellStyle(integerStyle);
            Cell spentCell = row.createCell(5);
            spentCell.setCellValue(dto.getTotalSpent().doubleValue());
            spentCell.setCellStyle(currencyStyle);
        }

        for (int i = 0; i < 6; i++) sheet.autoSizeColumn(i);
    }

    private void createOverviewSheet(Workbook workbook, CustomerOverviewDto overview) {
        Sheet sheet = workbook.createSheet("Tổng quan khách hàng");
        int rowNum = 0;

        Row row1 = sheet.createRow(rowNum++);
        row1.createCell(0).setCellValue("Tổng số khách hàng");
        row1.createCell(1).setCellValue(overview.getTotalCustomers());

        Row row2 = sheet.createRow(rowNum++);
        row2.createCell(0).setCellValue("Số khách hàng mới trong tháng");
        row2.createCell(1).setCellValue(overview.getNewCustomersThisMonth());

        Row row3 = sheet.createRow(rowNum++);
        row3.createCell(0).setCellValue("Số khách hàng đã mua ít nhất 1 đơn");
        row3.createCell(1).setCellValue(overview.getCustomersWithOrders());

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }
}