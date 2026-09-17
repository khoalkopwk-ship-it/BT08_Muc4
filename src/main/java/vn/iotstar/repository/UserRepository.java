package vn.iotstar.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.iotstar.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByUsernameIgnoreCase(String username);
    Optional<User> findByEmailIgnoreCase(String email);
    Optional<User> findFirstByUsernameIgnoreCaseOrEmailIgnoreCase(String username, String email);
    
    // Tìm kiếm người dùng theo tên đăng nhập, họ tên hoặc email (không phân biệt chữ hoa chữ thường)
    Page<User> findByUsernameContainingIgnoreCaseOrFullnameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String username, String fullname, String email, Pageable pageable);
    
    // Kiểm tra xem tên người dùng hoặc email đã tồn tại hay chưa (không phân biệt chữ hoa chữ thường)        
    boolean existsByUsernameIgnoreCase(String username);
    boolean existsByUsernameIgnoreCaseAndIdNot(String username, Long id);
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);
}