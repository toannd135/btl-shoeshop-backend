package vn.edu.ptit.shoe_shop.service.impl;

import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import vn.edu.ptit.shoe_shop.constant.StatusEnum;
import vn.edu.ptit.shoe_shop.dto.mapper.PermissionMapper;
import vn.edu.ptit.shoe_shop.dto.request.PermissionCreateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.request.PermissionUpdateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.response.PermissionResponseDTO;
import vn.edu.ptit.shoe_shop.entity.Permission;
import vn.edu.ptit.shoe_shop.exception.IdInvalidException;
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
       Permission permission = this.permissionRepository.findByPermissionId(id)
               .orElseThrow(() -> new IdInvalidException("Permission not found"));
       this.permissionMapper.updatePermissionEntityToDto(permissionRequestDTO, permission);
       return this.permissionMapper.toResponseDto(this.permissionRepository.save(permission));
    }

    @Override
    public void deletePermission(UUID id) {
        Permission permission = this.permissionRepository.findByPermissionId(id)
                .orElseThrow(() -> new IdInvalidException("Permission not found"));
        permission.setStatus(StatusEnum.DELETED);
        this.permissionRepository.save(permission);
    }


    @Override
    public PermissionResponseDTO fetchPermission(UUID id) {
        Permission permission = this.permissionRepository.findByPermissionId(id)
                .orElseThrow(() -> new IdInvalidException("Permission not found"));
        return this.permissionMapper.toResponseDto(permission);
    }
}
