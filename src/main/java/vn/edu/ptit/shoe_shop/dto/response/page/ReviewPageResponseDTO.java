package vn.edu.ptit.shoe_shop.dto.response.page;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.edu.ptit.shoe_shop.dto.response.ReviewResponse;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewPageResponseDTO extends PageResponseAbstractDTO {

    private List<ReviewResponse> items;

}