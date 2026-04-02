package vn.edu.ptit.shoe_shop.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Types;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "conversation")
@Builder
public class Conversation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "conversation_id", columnDefinition = "CHAR(36)")
    @JdbcTypeCode(Types.VARCHAR)
    UUID conversationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @ManyToOne(fetch = FetchType.LAZY )
    @JoinColumn(name = "admin_id", nullable = false)
    User admin;

    String lastMessage;

    @CreationTimestamp
    LocalDateTime createdAt;

    @UpdateTimestamp
    LocalDateTime updatedAt;

}
