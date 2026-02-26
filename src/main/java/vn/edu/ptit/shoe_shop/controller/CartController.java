package vn.edu.ptit.shoe_shop.controller;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import vn.edu.ptit.shoe_shop.dto.request.AddVariantRequestDTO;
import vn.edu.ptit.shoe_shop.dto.request.UpdateItemCartRequestDTO;
import vn.edu.ptit.shoe_shop.dto.response.ApiResponse;
import vn.edu.ptit.shoe_shop.dto.response.CartResponseDTO;
import vn.edu.ptit.shoe_shop.service.CartService;

@RestController
@RequestMapping("api/v1/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;
    @GetMapping("")
    public ResponseEntity<CartResponseDTO>getCart(){
        String id="123e4567-e89b-12d3-a456-426614174000";
        System.out.println("UserId ne:" + id);
        CartResponseDTO cartResponseDTO=this.cartService.getMyCart(id);
        return ResponseEntity.status(HttpStatus.OK).body(cartResponseDTO);
    }
    @PostMapping("")
    public ResponseEntity<ApiResponse<?>> addVariantToCart(@RequestBody @Valid AddVariantRequestDTO requestDTO)
    {
        ApiResponse<Object> apiResponse = this.cartService.addProductVariantToCart(requestDTO);
        return ResponseEntity.status(apiResponse.getStatusCode()).body(apiResponse);
    }
    @PostMapping("/update-quantity-item")
    public ResponseEntity<Void> updateQuantityItem(@RequestBody @Valid UpdateItemCartRequestDTO requestDTO)
    {
        this.cartService.updateQuantityItem(requestDTO);
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItemFromCart(@PathVariable String id)
    {
        this.cartService.deleteItemFromCart(id);
        return ResponseEntity.noContent().build();
    }
}
