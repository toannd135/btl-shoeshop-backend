package vn.edu.ptit.shoe_shop.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.edu.ptit.shoe_shop.entity.User;
@Repository
public interface UserRepository extends JpaRepository<User, UUID>{
}
