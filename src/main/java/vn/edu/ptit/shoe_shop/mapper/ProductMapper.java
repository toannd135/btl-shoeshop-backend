package vn.edu.ptit.shoe_shop.mapper;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import vn.edu.ptit.shoe_shop.dto.response.ProductResponseDTO;
import vn.edu.ptit.shoe_shop.entity.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductResponseDTO toResponse(Product product);
}
