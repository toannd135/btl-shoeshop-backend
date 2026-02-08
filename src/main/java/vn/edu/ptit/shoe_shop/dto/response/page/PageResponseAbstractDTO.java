package vn.edu.ptit.shoe_shop.dto.response.page;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageResponseAbstractDTO {
    private int page;
    private int pageSize;
    private int pages;
    private Long total;
}