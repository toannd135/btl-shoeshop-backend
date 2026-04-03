package vn.edu.ptit.shoe_shop.dto.response.Chat;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ListConversationForAdminResponse {
    private List<ConversationItemResponse> conversations;
}
