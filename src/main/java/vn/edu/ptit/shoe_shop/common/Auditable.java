package vn.edu.ptit.shoe_shop.common;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import vn.edu.ptit.shoe_shop.common.enums.StatusEnum;
import vn.edu.ptit.shoe_shop.common.utils.security.SecurityUtils;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@MappedSuperclass
@Getter
@Setter
public abstract class Auditable implements Serializable {

    @Column(name = "created_by", updatable = false)
    protected UUID createdBy;

    @Column(name = "updated_by")
    protected UUID updatedBy;

    @Column(updatable = false)
    protected Instant createdAt;

    protected Instant updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    protected StatusEnum status;

    @PrePersist
    protected void onCreate() {
        UUID userId = SecurityUtils.getCurrentUserId();

        this.createdBy = userId;
        this.updatedBy = userId;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();

        if (status == null) {
            status = StatusEnum.ACTIVE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedBy = SecurityUtils.getCurrentUserId();
        this.updatedAt = Instant.now();
    }
}