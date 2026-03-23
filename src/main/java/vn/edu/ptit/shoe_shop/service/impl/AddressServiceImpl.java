package vn.edu.ptit.shoe_shop.service.impl;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.ptit.shoe_shop.common.enums.StatusEnum;
import vn.edu.ptit.shoe_shop.common.exception.BadRequestException;
import vn.edu.ptit.shoe_shop.common.exception.IdInvalidException;
import vn.edu.ptit.shoe_shop.common.security.SecurityUtils;
import vn.edu.ptit.shoe_shop.dto.request.AddressRequestDTO;
import vn.edu.ptit.shoe_shop.dto.response.AddressResponseDTO;
import vn.edu.ptit.shoe_shop.dto.response.page.AddressPageResponseDTO;
import vn.edu.ptit.shoe_shop.entity.Address;
import vn.edu.ptit.shoe_shop.entity.User;
import vn.edu.ptit.shoe_shop.mapper.AddressMapper;
import vn.edu.ptit.shoe_shop.mapper.UserMapper;
import vn.edu.ptit.shoe_shop.repository.AddressRepository;
import vn.edu.ptit.shoe_shop.repository.UserRepository;
import vn.edu.ptit.shoe_shop.service.AddressService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AddressServiceImpl implements AddressService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;

    public AddressServiceImpl(UserMapper userMapper, AddressRepository addressRepository, AddressMapper addressMapper,
                                UserRepository userRepository) {
        this.userMapper = userMapper;
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.addressMapper = addressMapper;
    }

    @Override
    @Transactional
    public AddressResponseDTO createAddress(AddressRequestDTO addressRequestDTO) {
        UUID userId = SecurityUtils.getCurrentUserId();
        User user = this.userRepository.findByUserId(userId).orElseThrow(() -> new IdInvalidException("user not found"));
        Address address = this.addressMapper.toEntity(addressRequestDTO);
        boolean hasAnyAddress = this.addressRepository.existsByUser(user);
        if(!hasAnyAddress) {
            address.setIsDefault(true);
        }
        else if(Boolean.TRUE.equals(addressRequestDTO.getIsDefault())) {
            this.addressRepository.updateIsDefaultFalseByUser(user);
            address.setIsDefault(true);
        }
        else {
            address.setIsDefault(false);
        }
        address.setUser(user);
        this.addressRepository.save(address);
        return this.addressMapper.toResponseDto(address);
    }

    @Override
    public AddressResponseDTO updateAddress(AddressRequestDTO addressRequestDTO, UUID id) {
        UUID userId = SecurityUtils.getCurrentUserId();
        User user = this.userRepository.findByUserId(userId).orElseThrow(() -> new IdInvalidException("user not found"));
        Address address = this.addressRepository.findByAddressId(id)
                .orElseThrow(() -> new IdInvalidException("address not found"));
        if (!address.getUser().getUserId().equals(user.getUserId())) {
            throw new AccessDeniedException("You do not have permission to update this address.");
        }
        address.setReceiverName(addressRequestDTO.getReceiverName());
        address.setReceiverPhone(addressRequestDTO.getReceiverPhone());
        address.setStreet(addressRequestDTO.getStreet());
        address.setWard(addressRequestDTO.getWard());
        address.setDistrict(addressRequestDTO.getDistrict());
        address.setCity(addressRequestDTO.getCity());
        if (Boolean.TRUE.equals(addressRequestDTO.getIsDefault())) {
            this.addressRepository.updateIsDefaultFalseByUser(user);
            address.setIsDefault(true);
        } else if (address.getIsDefault() == null) {
                address.setIsDefault(false);
        }
        address.setUser(user);

        this.addressRepository.save(address);
        return this.addressMapper.toResponseDto(address);
    }

    @Override
    public AddressResponseDTO getAddress(UUID id) {
        UUID userId = SecurityUtils.getCurrentUserId();
        User user = this.userRepository.findByUserId(userId)
                .orElseThrow(() -> new IdInvalidException("user not found"));
        Address address = this.addressRepository.findByAddressId(id)
                .orElseThrow(() -> new IdInvalidException("address not found"));
        if (!address.getUser().getUserId().equals(user.getUserId())) {
            throw new AccessDeniedException("You do not have permission to update this address.");
        }
        return this.addressMapper.toResponseDto(address);
    }

    @Override
    public void deleteAddress(UUID id) {
        UUID userId = SecurityUtils.getCurrentUserId();
        User user = this.userRepository.findByUserId(userId)
                .orElseThrow(() -> new IdInvalidException("user not found"));
        Address address = this.addressRepository.findByAddressId(id)
                .orElseThrow(() -> new IdInvalidException("address not found"));
        if (!address.getUser().getUserId().equals(user.getUserId())) {
            throw new AccessDeniedException("You do not have permission to update this address.");
        }
        if(Boolean.TRUE.equals(address.getIsDefault())){
            throw new BadRequestException("The default address cannot be deleted.");
        }
        address.setStatus(StatusEnum.DELETED);
        this.addressRepository.save(address);
    }

    @Override
    public AddressPageResponseDTO getAllAddresses() {
        UUID userId = SecurityUtils.getCurrentUserId();
        User user = this.userRepository.findByUserId(userId)
                .orElseThrow(() -> new IdInvalidException("user not found"));

        List<Address> addresses = this.addressRepository.findByUser(user);

        List<AddressResponseDTO> addressResponseDTOs = addresses.stream()
                .filter(address -> address.getStatus() == StatusEnum.ACTIVE)
                .map(addressMapper::toResponseDto)
                .collect(Collectors.toList());

        AddressPageResponseDTO responseDTO = new AddressPageResponseDTO();
        responseDTO.setAddresses(addressResponseDTOs);
        responseDTO.setTotal((long) addressResponseDTOs.size());
        return responseDTO;
    }
}
