package vn.edu.ptit.shoe_shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.ptit.shoe_shop.entity.Permission;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PermissionRepository extends JpaRepository<Permission, UUID> {
    Optional<Permission> findByPermissionId(UUID id);
    List<Permission> findByPermissionIdIn(List<UUID> id);
    boolean existsByModuleAndApiPathAndMethod(String module, String apiPath, String method);
}
