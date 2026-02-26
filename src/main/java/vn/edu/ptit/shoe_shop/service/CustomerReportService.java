package vn.edu.ptit.shoe_shop.service;

import java.util.List;

import vn.edu.ptit.shoe_shop.dto.response.CustomerOverviewDto;
import vn.edu.ptit.shoe_shop.dto.response.TopCustomerDto;

public interface CustomerReportService {
    public CustomerOverviewDto getCustomerOverview();
    public List<TopCustomerDto> getTopSpenders(int limit) ;
}
