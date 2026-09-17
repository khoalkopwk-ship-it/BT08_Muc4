package vn.iotstar.dto;

import java.io.Serializable;

// DTO (Data Transfer Object) cho việc đăng ký người dùng đang chờ xử lý
// Không lưu mật khẩu thô trong session. Controller sẽ mã hóa BCrypt trước khi tạo object này.
public record PendingRegistration(
        String fullname,
        String username,
        String email,
        String phone,
        String passwordHash
) implements Serializable { }