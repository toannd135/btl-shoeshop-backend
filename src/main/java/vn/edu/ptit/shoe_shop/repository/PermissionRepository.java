package vn.edu.ptit.shoe_shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import vn.edu.ptit.shoe_shop.entity.Permission;
import vn.edu.ptit.shoe_shop.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, UUID>, QuerydslPredicateExecutor<Permission> {
    Optional<Permission> findByPermissionId(UUID id);
    List<Permission> findByPermissionIdIn(List<UUID> id);
    boolean existsByModuleAndApiPathAndMethod(String module, String apiPath, String method);
}
