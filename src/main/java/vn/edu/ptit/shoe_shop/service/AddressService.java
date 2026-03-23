package vn.edu.ptit.shoe_shop.service;

import vn.edu.ptit.shoe_shop.dto.request.AddressRequestDTO;
import vn.edu.ptit.shoe_shop.dto.response.AddressResponseDTO;
import vn.edu.ptit.shoe_shop.dto.response.page.AddressPageResponseDTO;
import java.util.UUID;

public interface AddressService {
    AddressResponseDTO createAddress(AddressRequestDTO addressRequestDTO);
    AddressResponseDTO updateAddress(AddressRequestDTO addressRequestDTO, UUID id);
    AddressResponseDTO getAddress(UUID id);
    void deleteAddress(UUID id);
    AddressPageResponseDTO getAllAddresses();
}
