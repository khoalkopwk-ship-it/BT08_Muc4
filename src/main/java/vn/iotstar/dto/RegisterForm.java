package vn.iotstar.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

// DTO (Data Transfer Object) cho form đăng ký người dùng
@Getter
@Setter
public class RegisterForm {
    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 100, message = "Họ tên tối đa 100 ký tự")
    private String fullname;

    @NotBlank(message = "Username không được để trống")
    @Pattern(regexp = "^[A-Za-z0-9._-]{3,50}$",
             message = "Username gồm 3-50 ký tự chữ, số, dấu chấm, _ hoặc -")
    private String username;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    @Size(max = 100)
    private String email;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^[0-9+ -]{9,20}$", message = "Số điện thoại không hợp lệ")
    private String phone;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, max = 100, message = "Mật khẩu phải từ 6 đến 100 ký tự")
    private String password;

    @NotBlank(message = "Vui lòng xác nhận mật khẩu")
    private String confirmPassword;
}