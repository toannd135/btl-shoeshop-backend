package vn.edu.ptit.shoe_shop.service;

import java.io.IOException;
import java.time.Instant;

public interface ReportExportService {
    byte[] exportAllReportsToExcel(Instant startDate, Instant endDate,
                                   int limitProduct, int limitCustomer) throws IOException;

    byte[] exportAllReportsToCsv(Instant startDate, Instant endDate,
                                  int limitProduct, int limitCustomer) throws IOException;
}
