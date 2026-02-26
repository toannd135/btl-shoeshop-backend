package vn.edu.ptit.shoe_shop.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.edu.ptit.shoe_shop.dto.request.search.PermissionSearchRequestDTO;
import vn.edu.ptit.shoe_shop.entity.Permission;

public interface PermissionRepositoryCustom {
    Page<Permission> searchPermissions(PermissionSearchRequestDTO request, Pageable pageable);
}
