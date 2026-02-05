package vn.edu.ptit.shoe_shop.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import vn.edu.ptit.shoe_shop.entity.Cart;


public interface CartRepository extends JpaRepository<Cart,UUID> {
     Optional<Cart> findByUser_UserId(UUID userId);
}
