package vn.edu.ptit.shoe_shop.common;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import vn.edu.ptit.shoe_shop.common.enums.StatusEnum;
import vn.edu.ptit.shoe_shop.entity.User;

import java.time.Instant;

@MappedSuperclass
@Getter
@Setter
public abstract class Auditable {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", updatable = false)
    protected User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    protected User updatedBy;

    @Column(updatable = false)
    protected Instant createdAt;

    protected Instant updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    StatusEnum status ;

    @PrePersist
    protected void onCreate() {

        if (status == null) {
            status = StatusEnum.ACTIVE;
        }
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }
}

