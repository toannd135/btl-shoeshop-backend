package vn.edu.ptit.shoe_shop.service.impl;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import vn.edu.ptit.shoe_shop.dto.mapper.RoleMapper;
import vn.edu.ptit.shoe_shop.dto.request.RoleCreateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.response.RoleResponseDTO;
import vn.edu.ptit.shoe_shop.entity.Role;
import vn.edu.ptit.shoe_shop.repository.RoleRepository;
import vn.edu.ptit.shoe_shop.service.RoleService;

import java.util.UUID;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleMapper roleMapper;
    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleMapper roleMapper, RoleRepository roleRepository) {
        this.roleMapper = roleMapper;
        this.roleRepository = roleRepository;
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
        role.setName(role.getName().toUpperCase());
        role.setCode(role.getCode().toUpperCase());
        //check permission
        this.roleRepository.save(role);
        return this.roleMapper.toResponseDTO(role);
    }
}
