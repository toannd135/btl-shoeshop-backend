package vn.edu.ptit.shoe_shop.service.impl;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import vn.edu.ptit.shoe_shop.constant.StatusEnum;
import vn.edu.ptit.shoe_shop.dto.mapper.RoleMapper;
import vn.edu.ptit.shoe_shop.dto.request.RoleCreateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.request.RoleUpdateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.response.RoleResponseDTO;
import vn.edu.ptit.shoe_shop.entity.Permission;
import vn.edu.ptit.shoe_shop.entity.Role;
import vn.edu.ptit.shoe_shop.exception.IdInvalidException;
import vn.edu.ptit.shoe_shop.repository.PermissionRepository;
import vn.edu.ptit.shoe_shop.repository.RoleRepository;
import vn.edu.ptit.shoe_shop.service.RoleService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleMapper roleMapper;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RoleServiceImpl(RoleMapper roleMapper, RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleMapper = roleMapper;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    @Override
    public RoleResponseDTO createRole(RoleCreateRequestDTO roleCreateRequestDTO) {
        if (this.roleRepository.existsByName(roleCreateRequestDTO.getName())) {
            throw new DataIntegrityViolationException("Role already exists with same name!");
        }
        if (this.roleRepository.existsByCode(roleCreateRequestDTO.getCode())) {
            throw new DataIntegrityViolationException("Role already exists with same code!");
        }
        Role role = this.roleMapper.toEntity(roleCreateRequestDTO);
        //check permission
        if(roleCreateRequestDTO.getPermissions() != null && !roleCreateRequestDTO.getPermissions().isEmpty()) {
            List<UUID> permissionIds = roleCreateRequestDTO.getPermissions().stream()
                    .map(RoleCreateRequestDTO.PermissionRoleCreateRequestDTO::getId).collect(Collectors.toList());
            List<Permission> permissions = this.permissionRepository.findByPermissionIdIn(permissionIds);
            role.setPermissions(permissions);
        }
        this.roleRepository.save(role);
        return this.roleMapper.toResponseDTO(role);
    }

    @Override
    public RoleResponseDTO updateRole(RoleUpdateRequestDTO roleUpdateRequestDTO, UUID id) {
        Role role = this.roleRepository.findByRoleId(id)
                .orElseThrow(() -> new IdInvalidException("Role not found"));
        this.roleMapper.updateRoleEntityToDto(roleUpdateRequestDTO, role);
        if (roleUpdateRequestDTO.getPermissions() != null) {
            List<UUID> permissions = roleUpdateRequestDTO.getPermissions()
                    .stream()
                    .map(RoleUpdateRequestDTO.RolePermissionUpdateRequestDTO::getId)
                    .collect(Collectors.toList());
            List<Permission> dbPermissions = this.permissionRepository.findByPermissionIdIn(permissions);
            role.setPermissions(dbPermissions);
        }
        return this.roleMapper.toResponseDTO(this.roleRepository.save(role));
    }

    @Override
    public RoleResponseDTO fetchRole(UUID id) {
        Role role = this.roleRepository.findByRoleId(id)
                .orElseThrow(() -> new IdInvalidException("Role not found"));
        return this.roleMapper.toResponseDTO(role);
    }

    @Override
    public void deleteRole(UUID id) {
        Role role = this.roleRepository.findByRoleId(id)
                .orElseThrow(() -> new IdInvalidException("Role not found"));
        role.setStatus(StatusEnum.DELETED);
        this.roleRepository.save(role);
    }
}
