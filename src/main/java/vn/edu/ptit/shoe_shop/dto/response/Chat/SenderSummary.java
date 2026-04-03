package vn.edu.ptit.shoe_shop.dto.response.Chat;

import java.util.UUID;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SenderSummary {
    String senderId;
    String senderName;
    String avatar;
}
