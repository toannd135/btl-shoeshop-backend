package vn.edu.ptit.shoe_shop.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.ptit.shoe_shop.common.utils.annotation.ApiMessage;
import vn.edu.ptit.shoe_shop.dto.request.AddressRequestDTO;
import vn.edu.ptit.shoe_shop.dto.response.AddressResponseDTO;
import vn.edu.ptit.shoe_shop.dto.response.page.AddressPageResponseDTO;
import vn.edu.ptit.shoe_shop.service.AddressService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }


    @PostMapping("/addresses")
    @ApiMessage("Create address successfully")
    public ResponseEntity<?> addAddress(@RequestBody @Valid AddressRequestDTO addressRequestDTO) {
        AddressResponseDTO res = this.addressService.createAddress(addressRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @PutMapping("/addresses/{id}")
    @ApiMessage("Update address successfully")
    public ResponseEntity<?> updateAddress(@Valid @RequestBody AddressRequestDTO addressRequestDTO,
                                           @PathVariable UUID id) {
        AddressResponseDTO res = this.addressService.updateAddress(addressRequestDTO, id);
        return ResponseEntity.ok().body(res);
    }

    @DeleteMapping("/addresses/{id}")
    @ApiMessage("Delete address successfully")
    public ResponseEntity<?> deleteAddress(@PathVariable UUID id) {
        this.addressService.deleteAddress(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/addresses/{id}")
    @ApiMessage("Get addresses successfully")
    public ResponseEntity<?> fetchAddress(@PathVariable UUID id) {
        AddressResponseDTO res = this.addressService.getAddress(id);
        return ResponseEntity.ok().body(res);
    }

    @GetMapping("/addresses")
    @ApiMessage("Get all addresses successfully")
    public ResponseEntity<?> getAllAddresses() {
        AddressPageResponseDTO res = this.addressService.getAllAddresses();
        return ResponseEntity.ok().body(res);
    }

}
