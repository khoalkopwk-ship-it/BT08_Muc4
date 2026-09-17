package vn.iotstar.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "Categories",
       uniqueConstraints = @UniqueConstraint(columnNames = "category_name"))
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long categoryId;

    @NotBlank(message = "Tên danh mục không được để trống")
    @Size(max = 100, message = "Tên danh mục tối đa 100 ký tự")
    @Column(name = "category_name", nullable = false, length = 100,
            columnDefinition = "NVARCHAR(100)")
    private String categoryName;

    @Size(max = 255)
    @Column(name = "icon", length = 255, columnDefinition = "NVARCHAR(255)")
    private String icon;

    // Trạng thái: 0 - Không hoạt động, 1 - Hoạt động
    @Min(value = 0, message = "Trạng thái không hợp lệ")
    @Max(value = 1, message = "Trạng thái không hợp lệ")
    @Column(name = "status", nullable = false)
    private int status = 1;

    // Một danh mục có thể có nhiều sản phẩm
    @OneToMany(mappedBy = "category")
    private List<Product> products = new ArrayList<>();

    //không thêm cascade = CascadeType.REMOVE vì khi xóa danh mục thì sản phẩm vẫn tồn tại
}