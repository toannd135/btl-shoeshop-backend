package vn.edu.ptit.shoe_shop.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import vn.edu.ptit.shoe_shop.dto.response.CartItemResponseDTO;
import vn.edu.ptit.shoe_shop.dto.response.CartResponseDTO;
import vn.edu.ptit.shoe_shop.dto.response.VariantDTO;
import vn.edu.ptit.shoe_shop.entity.Cart;
import vn.edu.ptit.shoe_shop.entity.CartItem;

@Mapper(componentModel = "spring")
public interface CartMapper {
    @Mapping(target = "items", source = "items")
    CartResponseDTO toResponseDTO(Cart cart);

    @Mapping(target = "variant", source = ".")
    CartItemResponseDTO toItemDTO(CartItem item);

    @Mapping(target = "productVariantId", source = "variant.productVariantId")
    @Mapping(target = "sku", source = "variant.sku")
    @Mapping(target = "size", source = "variant.size")
    @Mapping(target = "color", source = "variant.color")
    @Mapping(
        target = "price",
        expression = "java(item.getVariant().getBasePrice())"
    )
    @Mapping(
        target = "imageUrl",
        expression = "java(item.getVariant().getListProductVariantImages().stream().filter(v -> Boolean.TRUE.equals(v.getIsPrimary())).findFirst().map(v -> v.getImageUrl()).orElse(null))"
    )
    VariantDTO toVariantDTO(CartItem item);
}
