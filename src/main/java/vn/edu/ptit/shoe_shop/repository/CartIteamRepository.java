package vn.edu.ptit.shoe_shop.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import vn.edu.ptit.shoe_shop.entity.CartItem;


public interface CartIteamRepository extends JpaRepository<CartItem,UUID> {
    Optional<CartItem> findByCartItemId(UUID cartItemId);
}
