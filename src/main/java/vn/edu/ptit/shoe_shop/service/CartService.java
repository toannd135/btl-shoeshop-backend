package vn.edu.ptit.shoe_shop.service;


import java.util.UUID;

import vn.edu.ptit.shoe_shop.dto.request.AddVariantRequestDTO;
import vn.edu.ptit.shoe_shop.dto.request.UpdateItemCartRequestDTO;
import vn.edu.ptit.shoe_shop.dto.response.ApiResponse;
import vn.edu.ptit.shoe_shop.dto.response.CartResponseDTO;
import vn.edu.ptit.shoe_shop.exception.IdInvalidException;

public interface CartService {
    public CartResponseDTO getMyCart(String userId) throws IdInvalidException;
    public ApiResponse<Object> addProductVariantToCart(AddVariantRequestDTO requestDTO);
    public void updateQuantityItem(UpdateItemCartRequestDTO requestDTO);
    public void deleteItemFromCart(String itemId);
}
