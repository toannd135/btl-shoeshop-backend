package vn.edu.ptit.shoe_shop.dto.response.page;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.edu.ptit.shoe_shop.dto.response.RoleResponseDTO;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RolePageResponseDTO extends PageResponseAbstractDTO{
    List<RoleResponseDTO> roles;
}
