package vn.iotstar.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import vn.iotstar.entity.User;
import vn.iotstar.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.username}") private String username;
    @Value("${app.admin.password}") private String password;
    @Value("${app.admin.email}") private String email;

    // Khởi tạo dữ liệu ban đầu cho ứng dụng, bao gồm tạo tài khoản quản trị nếu chưa tồn tại
    @Override
    public void run(String... args) {
        if (userRepository.findByUsernameIgnoreCase(username).isPresent()) return;
        User admin = new User();
        admin.setUsername(username);
        admin.setPassword(passwordEncoder.encode(password));
        admin.setFullname("Administrator");
        admin.setEmail(email);
        admin.setImages("images/admin.png");
        admin.setRole("ADMIN");
        admin.setEnabled(true);
        userRepository.save(admin);
    }
}