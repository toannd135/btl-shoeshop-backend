package vn.edu.ptit.shoe_shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import vn.edu.ptit.shoe_shop.entity.Role;
import vn.edu.ptit.shoe_shop.entity.User;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID>, QuerydslPredicateExecutor<Role> {
    Optional<Role> findByRoleId(UUID roleId);
    boolean existsByName(String name);
    boolean existsByCode(String code);
    Optional<Role> findByName(String roleName);
}
