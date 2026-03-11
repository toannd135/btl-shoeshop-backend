package vn.edu.ptit.shoe_shop.common;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import vn.edu.ptit.shoe_shop.common.utils.security.SecurityUtils;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@MappedSuperclass
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class Auditable implements Serializable {

    @Column(name = "created_by", updatable = false)
    UUID createdBy;

    @Column(name = "updated_by")
    UUID updatedBy;

    @Column(updatable = false)
    Instant createdAt;

    Instant updatedAt;


    @PrePersist
    protected void onCreate() {
        UUID userId = SecurityUtils.getCurrentUserId();

        this.createdBy = userId;
        this.updatedBy = userId;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();

    }

    @PreUpdate
    void onUpdate() {
        this.updatedBy = SecurityUtils.getCurrentUserId();
        this.updatedAt = Instant.now();
    }
}