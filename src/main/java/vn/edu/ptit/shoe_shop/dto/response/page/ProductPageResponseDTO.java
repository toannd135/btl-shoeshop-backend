package vn.edu.ptit.shoe_shop.dto.response.page;

import lombok.*;
import vn.edu.ptit.shoe_shop.dto.response.ProductResponseDTO;


import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductPageResponseDTO extends  PageResponseAbstractDTO{
    private  List<ProductResponseDTO> items;
}
