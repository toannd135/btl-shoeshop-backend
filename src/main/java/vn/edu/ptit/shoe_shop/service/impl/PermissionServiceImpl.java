package vn.edu.ptit.shoe_shop.service.impl;

import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import vn.edu.ptit.shoe_shop.dto.mapper.PermissionMapper;
import vn.edu.ptit.shoe_shop.dto.request.PermissionCreateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.request.PermissionUpdateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.response.PermissionResponseDTO;
import vn.edu.ptit.shoe_shop.entity.Permission;
import vn.edu.ptit.shoe_shop.repository.PermissionRepository;
import vn.edu.ptit.shoe_shop.service.PermissionService;

@Service
public class PermissionServiceImpl implements PermissionService {
    private final PermissionRepository permissionRepository;
    private final PermissionMapper permissionMapper;

    public PermissionServiceImpl(PermissionRepository permissionRepository, PermissionMapper permissionMapper) {
        this.permissionRepository = permissionRepository;
        this.permissionMapper = permissionMapper;
    }

    @Override
    public PermissionResponseDTO createPermission(PermissionCreateRequestDTO permissionCreateRequestDTO) {
        if (permissionCreateRequestDTO.getApiPath() != null && !permissionCreateRequestDTO.getApiPath().isEmpty()
                && permissionCreateRequestDTO.getModule() != null && !permissionCreateRequestDTO.getModule().isEmpty()
                && permissionCreateRequestDTO.getMethod() != null && !permissionCreateRequestDTO.getMethod().isEmpty()
                && this.permissionRepository.existsByModuleAndApiPathAndMethod(permissionCreateRequestDTO.getModule(), permissionCreateRequestDTO.getApiPath(), permissionCreateRequestDTO.getMethod())) {
            throw new DataIntegrityViolationException("Permission already exists with same module, apiPath and method");
        }
        Permission permission = this.permissionMapper.toEntity(permissionCreateRequestDTO);
        return this.permissionMapper.toResponseDto(this.permissionRepository.save(permission));
    }

    @Override
    public PermissionResponseDTO updatePermission(PermissionUpdateRequestDTO permissionRequestDTO, UUID id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updatePermission'");
    }

    @Override
    public void deletePermission(UUID id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deletePermission'");
    }

    @Override
    public PermissionResponseDTO fetchPermission(UUID id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'fetchPermission'");
    }
}
