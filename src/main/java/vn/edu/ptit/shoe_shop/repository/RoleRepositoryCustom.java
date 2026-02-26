package vn.edu.ptit.shoe_shop.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.edu.ptit.shoe_shop.dto.request.search.RoleSearchRequestDTO;
import vn.edu.ptit.shoe_shop.dto.request.search.UserSearchRequestDTO;
import vn.edu.ptit.shoe_shop.entity.Role;

public interface RoleRepositoryCustom {
    Page<Role> searchRoles(RoleSearchRequestDTO request, Pageable pageable);
}
