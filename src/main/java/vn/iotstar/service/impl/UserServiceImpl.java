package vn.iotstar.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vn.iotstar.dto.PendingRegistration;
import vn.iotstar.entity.User;
import vn.iotstar.repository.UserRepository;
import vn.iotstar.service.IUserService;
import vn.iotstar.service.ImageStorageService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
// Dịch vụ triển khai cho người dùng
// Giao diện dịch vụ triển khai cho người dùng
// Cung cấp các phương thức để quản lý người dùng, bao gồm tìm kiếm, lưu
public class UserServiceImpl implements IUserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ImageStorageService imageStorageService;

    @Override
    @Transactional(readOnly = true)
    public Page<User> findAll(String keyword, Pageable pageable) {
        if (keyword == null || keyword.isBlank()) return userRepository.findAll(pageable);
        String value = keyword.trim();
        return userRepository
                .findByUsernameContainingIgnoreCaseOrFullnameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        value, value, value, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsernameIgnoreCase(username);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean duplicateUsername(User user) {
        String value = user.getUsername().trim();
        return user.getId() == null
                ? userRepository.existsByUsernameIgnoreCase(value)
                : userRepository.existsByUsernameIgnoreCaseAndIdNot(value, user.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean duplicateEmail(User user) {
        String value = user.getEmail().trim();
        return user.getId() == null
                ? userRepository.existsByEmailIgnoreCase(value)
                : userRepository.existsByEmailIgnoreCaseAndIdNot(value, user.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean usernameExists(String username) {
        return userRepository.existsByUsernameIgnoreCase(username.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean emailExists(String email) {
        return userRepository.existsByEmailIgnoreCase(email.trim());
    }

    @Override
    public User save(User input) {
        if (input.getId() == null) {
            if (input.getPassword() == null || input.getPassword().isBlank()) {
                throw new IllegalArgumentException("Mật khẩu không được để trống");
            }
            normalize(input);
            input.setPassword(passwordEncoder.encode(input.getPassword()));
            return userRepository.save(input);
        }

        User current = userRepository.findById(input.getId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng"));
        current.setUsername(input.getUsername().trim());
        current.setFullname(input.getFullname().trim());
        current.setEmail(input.getEmail().trim().toLowerCase());
        current.setPhone(cleanPhone(input.getPhone()));
        current.setRole(input.getRole());
        current.setEnabled(input.isEnabled());
        if (input.getPassword() != null && !input.getPassword().isBlank()) {
            current.setPassword(passwordEncoder.encode(input.getPassword()));
        }
        return userRepository.save(current);
    }

    @Override
    public User registerVerified(PendingRegistration pending) {
        if (usernameExists(pending.username()) || emailExists(pending.email())) {
            throw new IllegalArgumentException("Username hoặc email đã được sử dụng");
        }
        User user = new User();
        user.setFullname(pending.fullname().trim());
        user.setUsername(pending.username().trim());
        user.setEmail(pending.email().trim().toLowerCase());
        user.setPhone(cleanPhone(pending.phone()));
        user.setPassword(pending.passwordHash());
        user.setRole("USER");
        user.setEnabled(true);
        user.setImages("images/admin.png");
        return userRepository.save(user);
    }

    @Override
    public User updateProfile(String username, String fullname, String phone, MultipartFile image) {
        User user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản"));
        user.setFullname(fullname.trim());
        user.setPhone(cleanPhone(phone));
        String uploaded = imageStorageService.store(image, "users");
        if (uploaded != null) user.setImages(uploaded);
        return userRepository.save(user);
    }

    @Override
    public void resetPassword(String email, String rawPassword) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new IllegalArgumentException("Email không tồn tại"));
        user.setPassword(passwordEncoder.encode(rawPassword));
        userRepository.save(user);
    }

    @Override
    public void deleteById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("Không tìm thấy người dùng");
        }
        userRepository.deleteById(id);
    }

    private void normalize(User user) {
        user.setUsername(user.getUsername().trim());
        user.setFullname(user.getFullname().trim());
        user.setEmail(user.getEmail().trim().toLowerCase());
        user.setPhone(cleanPhone(user.getPhone()));
        if (user.getRole() == null || user.getRole().isBlank()) user.setRole("USER");
    }

    private String cleanPhone(String phone) {
        if (phone == null) return null;
        String value = phone.trim();
        return value.isEmpty() ? null : value;
    }
}