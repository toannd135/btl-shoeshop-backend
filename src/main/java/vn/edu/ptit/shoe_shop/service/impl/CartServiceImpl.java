package vn.edu.ptit.shoe_shop.service.impl;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import vn.edu.ptit.shoe_shop.constant.enums.ProductStatusEnum;
import vn.edu.ptit.shoe_shop.dto.mapper.CartMapper;
import vn.edu.ptit.shoe_shop.dto.request.AddVariantRequestDTO;
import vn.edu.ptit.shoe_shop.dto.request.UpdateItemCartRequestDTO;
import vn.edu.ptit.shoe_shop.dto.response.ApiResponse;
import vn.edu.ptit.shoe_shop.dto.response.CartResponseDTO;
import vn.edu.ptit.shoe_shop.entity.Cart;
import vn.edu.ptit.shoe_shop.entity.CartItem;
import vn.edu.ptit.shoe_shop.entity.ProductVariant;
import vn.edu.ptit.shoe_shop.exception.BusinessException;
import vn.edu.ptit.shoe_shop.exception.IdInvalidException;
import vn.edu.ptit.shoe_shop.exception.NotFoundException;
import vn.edu.ptit.shoe_shop.repository.CartIteamRepository;
import vn.edu.ptit.shoe_shop.repository.CartRepository;
import vn.edu.ptit.shoe_shop.repository.ProductVariantRepository;
import vn.edu.ptit.shoe_shop.service.CartService;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService  {
    private final CartMapper cartMapper;
    private final CartRepository cartRepository;
    private final ProductVariantRepository productVariantRepository;
    private final CartIteamRepository cartIteamRepository;
@Override
@Transactional
public ApiResponse<Object> addProductVariantToCart(AddVariantRequestDTO requestDTO) {
    UUID userId;
    UUID variantId;
    try {
        userId = UUID.fromString(requestDTO.getUserId());
        variantId = UUID.fromString(requestDTO.getVariantId());
    } catch (IllegalArgumentException e) {
        throw new BusinessException("ID không đúng định dạng UUID");
    }

    if (requestDTO.getQuantity() <= 0) {
        throw new BusinessException("Số lượng phải lớn hơn 0");
    }

    // Tìm Cart và Variant tương ứng
    Cart cart = this.cartRepository.findByUser_UserId(userId)
            .orElseThrow(() -> new NotFoundException("Không xác thực được người dùng hoặc chưa có giỏ hàng"));

    ProductVariant variant = this.productVariantRepository.findByProductVariantId(variantId)
            .orElseThrow(() -> new NotFoundException("Không tìm thấy sản phẩm"));

    // Kiểm tra trạng thái sản phẩm xem còn hàng không hoặc còn bán không
    if (variant.getStatus().equals(ProductStatusEnum.OUT_OF_STOCK) || variant.getQuantity() < 1) {
        throw new BusinessException("Sản phẩm đã hết hàng hoặc ngừng bán!");
    }

    // Kiểm tra sô lượng khách mua có lớn hơn tồn kho không
    if (variant.getQuantity() < requestDTO.getQuantity()) {
        throw new BusinessException("Số lượng trong kho không đủ (Còn lại: " + variant.getQuantity() + ")");
    }

    //  Kiểm tra xem sản phẩm đã có trong giỏ hàng chưa
    // Nếu có rồi => Cộng dồn số lượng. Nếu chưa => Tạo mới.
    Optional<CartItem> existingItemOpt = cart.getItems().stream()
            .filter(item -> item.getVariant().getProductVariantId().equals(variantId))
            .findFirst();

    if (existingItemOpt.isPresent()) {
        CartItem existingItem = existingItemOpt.get();
        int newQuantity = existingItem.getQuantity() + requestDTO.getQuantity();

        // Kiểm tra lại tồn kho với tổng số lượng mới
        if (variant.getQuantity() < newQuantity) {
            throw new BusinessException("Tổng số lượng sản phẩm trong giỏ hàng vượt quá tồn kho cho phép");
        }
        existingItem.setQuantity(newQuantity);
    } else {
        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setVariant(variant);
        cartItem.setQuantity(requestDTO.getQuantity());
        cart.getItems().add(cartItem);
    }

    //Lưu lại
    this.cartRepository.save(cart);

    return new ApiResponse<>(200, null, "Thêm vào giỏ hàng thành công", "");
}

     @Override
    public CartResponseDTO getMyCart(String userId) throws IdInvalidException {
       UUID id;
        try {
            id = UUID.fromString(userId);
             Cart cart = this.cartRepository.findByUser_UserId(id).orElseThrow(()->new NotFoundException("Không xác thực được người dùng"));
             return this.cartMapper.toResponseDTO(cart);
        } catch (Exception e) {
            throw new IdInvalidException("Id không đúng định dạng");
        }
    }

     @Override
     public void updateQuantityItem(UpdateItemCartRequestDTO requestDTO) {
        UUID cartItemId;
        try {
            cartItemId= UUID.fromString(requestDTO.getCartItemId());
        } catch (Exception e) {
            throw new IdInvalidException("Id không đúng định dạng");
        }
        CartItem cartItem= this.cartIteamRepository.findByCartItemId(cartItemId)
        .orElseThrow(()-> new NotFoundException("Không tìm thấy sản phẩm"));
        // Nếu nhỏ hơn =0 thì xóa khỏi giỏ hàng luôn
        int newQty = cartItem.getQuantity() + requestDTO.getStep().getValue();
        if(newQty<=0)
        {
            this.cartIteamRepository.delete(cartItem);
        }else{
            // Nếu lớn hơn 0 thì cập nhật số lượng
            cartItem.setQuantity(newQty);
        }
        // Lưu lại
        this.cartIteamRepository.save(cartItem);
     }

     @Override
     @Transactional
     public void deleteItemFromCart(String itemId) {
        UUID deleteItemId;
        try {
            deleteItemId = UUID.fromString(itemId);
        } catch (Exception e) {
            throw new IdInvalidException("Id không đúng định dạng!");
        }
        this.cartIteamRepository.findByCartItemId(deleteItemId)
        .ifPresentOrElse(this.cartIteamRepository::delete,
             () -> { throw new NotFoundException("Không tìm thấy sản phẩm trong giỏ hàng"); }
        );
     }
     
}
