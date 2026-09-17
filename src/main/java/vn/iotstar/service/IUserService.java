package vn.iotstar.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import vn.iotstar.dto.PendingRegistration;
import vn.iotstar.entity.User;

import java.util.Optional;

// Giao diện dịch vụ cho người dùng
public interface IUserService {
    Page<User> findAll(String keyword, Pageable pageable);
    Optional<User> findById(Long id);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean duplicateUsername(User user);
    boolean duplicateEmail(User user);
    boolean usernameExists(String username);
    boolean emailExists(String email);
    User save(User user);
    User registerVerified(PendingRegistration pending);
    User updateProfile(String username, String fullname, String phone, MultipartFile image);
    void resetPassword(String email, String rawPassword);
    void deleteById(Long id);
}