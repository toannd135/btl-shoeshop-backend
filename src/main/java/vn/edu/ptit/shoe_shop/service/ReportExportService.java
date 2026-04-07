package vn.edu.ptit.shoe_shop.service;

import java.io.IOException;
import java.time.Instant;

public interface ReportExportService {
    public byte[] exportAllReportsToExcel(Instant startDate, Instant endDate,
                                          int limitProduct, int limitCustomer) throws IOException;
}
