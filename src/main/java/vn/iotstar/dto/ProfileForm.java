package vn.iotstar.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

// DTO (Data Transfer Object) cho form cập nhật thông tin người dùng
public class ProfileForm {
    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 100, message = "Họ tên tối đa 100 ký tự")
    private String fullname;

    @Pattern(regexp = "^$|^[0-9+ -]{9,20}$", message = "Số điện thoại không hợp lệ")
    private String phone;
}