package vn.edu.ptit.shoe_shop.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.edu.ptit.shoe_shop.entity.CartItem;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem,UUID> {
    Optional<CartItem> findByCartItemId(UUID cartItemId);
}
