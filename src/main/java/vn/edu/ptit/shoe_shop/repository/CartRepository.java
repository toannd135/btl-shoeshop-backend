package vn.edu.ptit.shoe_shop.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.edu.ptit.shoe_shop.entity.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart,UUID> {
     Optional<Cart> findByUser_UserId(UUID userId);
     Optional<Cart> findByCartId(UUID cartId);

     @Modifying // Bắt buộc phải có khi dùng custom delete/update
    @Query("DELETE FROM CartItem c WHERE c.cart.cartId = :cartId")
    void deleteAllByCart_CartId(@Param("cartId") UUID cartId);
}
