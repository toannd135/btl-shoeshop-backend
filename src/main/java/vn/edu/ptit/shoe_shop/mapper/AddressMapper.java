package vn.edu.ptit.shoe_shop.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vn.edu.ptit.shoe_shop.dto.request.AddressRequestDTO;
import vn.edu.ptit.shoe_shop.dto.response.AddressResponseDTO;
import vn.edu.ptit.shoe_shop.entity.Address;

@Mapper(componentModel = "spring")
public interface AddressMapper {
    @Mapping(target = "isDefault", ignore = true)
    Address toEntity(AddressRequestDTO addressRequestDTO);


    AddressResponseDTO toResponseDto(Address address);
}
