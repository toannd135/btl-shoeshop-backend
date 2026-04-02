package vn.edu.ptit.shoe_shop.dto.response.page;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.edu.ptit.shoe_shop.dto.response.Chat.ChatMessageResponse;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessagePageResponseDTO extends PageResponseAbstractDTO {
    private List<ChatMessageResponse> items;
}