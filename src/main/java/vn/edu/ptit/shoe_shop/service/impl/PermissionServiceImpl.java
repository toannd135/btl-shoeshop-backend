package vn.edu.ptit.shoe_shop.service.impl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.edu.ptit.shoe_shop.common.enums.StatusEnum;
import vn.edu.ptit.shoe_shop.dto.request.search.PermissionSearchRequestDTO;
import vn.edu.ptit.shoe_shop.dto.response.page.PermissionPageResponseDTO;
import vn.edu.ptit.shoe_shop.mapper.PermissionMapper;
import vn.edu.ptit.shoe_shop.dto.request.PermissionCreateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.request.PermissionUpdateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.response.PermissionResponseDTO;
import vn.edu.ptit.shoe_shop.entity.Permission;
import vn.edu.ptit.shoe_shop.common.exception.IdInvalidException;
import vn.edu.ptit.shoe_shop.repository.PermissionRepository;
import vn.edu.ptit.shoe_shop.repository.PermissionRepositoryCustom;
import vn.edu.ptit.shoe_shop.service.PermissionService;

@Service
public class PermissionServiceImpl implements PermissionService {
    private final PermissionRepository permissionRepository;
    private final PermissionMapper permissionMapper;
    private final PermissionRepositoryCustom permissionRepositoryCustom;
    public PermissionServiceImpl(PermissionRepository permissionRepository, PermissionMapper permissionMapper, PermissionRepositoryCustom permissionRepositoryCustom) {
        this.permissionRepository = permissionRepository;
        this.permissionMapper = permissionMapper;
        this.permissionRepositoryCustom = permissionRepositoryCustom;
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

    @Override
    public PermissionPageResponseDTO searchPermissions(PermissionSearchRequestDTO request, Pageable pageable) {
        Page<Permission> permissionPage = this.permissionRepositoryCustom.searchPermissions(request, pageable);
        List<PermissionResponseDTO> permissionResponseDTOS = permissionPage.getContent()
                .stream()
                .map(this.permissionMapper::toResponseDto)
                .collect(Collectors.toList());
        PermissionPageResponseDTO res = new PermissionPageResponseDTO();
        res.setPermissions(permissionResponseDTOS);
        res.setPage(permissionPage.getNumber() + 1);
        res.setPageSize(permissionPage.getSize());
        res.setPages(permissionPage.getTotalPages());
        res.setTotal(permissionPage.getTotalElements());
        return res;
    }
}
