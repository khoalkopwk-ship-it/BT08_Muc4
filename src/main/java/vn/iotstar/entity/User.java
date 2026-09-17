package vn.iotstar.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "Users", uniqueConstraints = {
        @UniqueConstraint(name = "UQ_Users_Username", columnNames = "username"),
        @UniqueConstraint(name = "UQ_Users_Email", columnNames = "email")
})
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username không được để trống")
    @Size(max = 50, message = "Username tối đa 50 ký tự")
    @Column(nullable = false, length = 50)
    private String username;

    @Size(max = 255, message = "Mật khẩu tối đa 255 ký tự")
    @Column(nullable = false, length = 255)
    private String password;

    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 100, message = "Họ tên tối đa 100 ký tự")
    @Column(nullable = false, length = 100, columnDefinition = "NVARCHAR(100)")
    private String fullname;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    @Size(max = 100, message = "Email tối đa 100 ký tự")
    @Column(nullable = false, length = 100)
    private String email;

    @Pattern(regexp = "^$|^[0-9+ -]{9,20}$", message = "Số điện thoại không hợp lệ")
    @Column(length = 20)
    private String phone;

    @Column(length = 255, columnDefinition = "NVARCHAR(255)")
    private String images;

    // Vai trò của người dùng: ADMIN hoặc USER
    @NotBlank(message = "Vai trò không được để trống")
    @Pattern(regexp = "ADMIN|USER", message = "Vai trò chỉ nhận ADMIN hoặc USER")
    @Column(nullable = false, length = 20)
    private String role = "USER";

    @Column(nullable = false)
    private boolean enabled = true;

    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    // Thiết lập ngày tạo trước khi lưu vào cơ sở dữ liệu
    @PrePersist
    void prePersist() {
        if (createdDate == null) createdDate = LocalDateTime.now();
    }
}