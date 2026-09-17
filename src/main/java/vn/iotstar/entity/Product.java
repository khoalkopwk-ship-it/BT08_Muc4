package vn.iotstar.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "Products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    @NotBlank(message = "Tên sản phẩm không được để trống")
    @Size(max = 255, message = "Tên sản phẩm tối đa 255 ký tự")
    @Column(name = "product_name", nullable = false, length = 255,
            columnDefinition = "NVARCHAR(255)")
    private String productName;

    @NotNull(message = "Giá không được để trống")
    @DecimalMin(value = "0.0", inclusive = true, message = "Giá không được âm")
    @Digits(integer = 16, fraction = 2, message = "Giá không hợp lệ")
    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal price;

    @Size(max = 2000, message = "Mô tả tối đa 2000 ký tự")
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @Column(length = 255, columnDefinition = "NVARCHAR(255)")
    private String image;

    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @PrePersist
    void prePersist() {
        if (createdDate == null) createdDate = LocalDateTime.now();
    }
}