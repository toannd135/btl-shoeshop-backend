package vn.edu.ptit.shoe_shop.entity;

import jakarta.persistence.*;
import vn.edu.ptit.shoe_shop.constant.RoleEnum;
import vn.edu.ptit.shoe_shop.constant.StatusEnum;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "category_id", updatable = false, nullable = false)
    private UUID categoryId;

    @Column(nullable = false, unique = true)
    private String categoryName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(
            mappedBy = "category",
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE}
    )
    private Set<Product> products = new HashSet<>();

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusEnum status;

    @Column(name = "createdAt", nullable = false)
    private Instant createdAt;

    @Column(name = "createdBy", nullable = false)
    private String createdBy;

    @Column(name = "updatedAt")
    private Instant updatedAt;

    @Column(name = "updatedBy")
    private String updatedBy;

    @PrePersist
    public void handleBeforeCreate() {
        this.createdBy = String.valueOf(this.userId);
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
        if (this.status == null) {
            this.status = StatusEnum.ACTIVE;
        }
    }

    @PreUpdate
    public void handleBeforeUpdate() {
        this.createdBy = String.valueOf(this.userId);
        this.updatedAt = Instant.now();
    }
    protected Category() {}

    public Category(String categoryName) {
        this.categoryName = categoryName;
    }


    public UUID getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public Category getParent() {
        return parent;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public void setParent(Category parent) {
        this.parent = parent;
    }
}
