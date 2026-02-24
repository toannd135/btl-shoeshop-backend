package vn.edu.ptit.shoe_shop.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import vn.edu.ptit.shoe_shop.dto.UserCredentialDTO;
import vn.edu.ptit.shoe_shop.entity.User;
@Repository
public interface UserRepository extends JpaRepository<User, UUID>, QuerydslPredicateExecutor<User> {
    Optional<User> findByUserId(UUID userId);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);

    @Query("SELECT new vn.edu.ptit.shoe_shop.dto.UserCredentialDTO(u.username, u.email) FROM User u")
    List<UserCredentialDTO> findAllUserCredentials();
}
