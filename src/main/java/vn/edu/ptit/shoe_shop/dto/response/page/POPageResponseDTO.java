package vn.edu.ptit.shoe_shop.dto.response.page;


import lombok.*;
import vn.edu.ptit.shoe_shop.dto.response.POSummaryResponse;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class POPageResponseDTO extends PageResponseAbstractDTO {
    private List<POSummaryResponse> items;
}