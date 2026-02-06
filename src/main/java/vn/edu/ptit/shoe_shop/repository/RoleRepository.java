package vn.edu.ptit.shoe_shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.ptit.shoe_shop.entity.Role;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {
    Optional<Role> findByRoleId(UUID roleId);
    boolean existsByName(String name);
    boolean existsByCode(String code);
}
