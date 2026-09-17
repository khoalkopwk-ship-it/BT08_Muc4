package vn.iotstar.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.iotstar.entity.Category;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Containing: SQL tương đương LIKE %keyword%.
    // IgnoreCase: không phân biệt chữ hoa/chữ thường.
    // Pageable: truyền số trang, kích thước trang và cách sắp xếp.
    // Page<Category>: trả cả dữ liệu lẫn tổng số trang, tổng số dòng.
    Page<Category> findByCategoryNameContainingIgnoreCase(String keyword, Pageable pageable);
    List<Category> findByStatusOrderByCategoryNameAsc(int status);
    
    // Kiểm tra xem tên danh mục đã tồn tại hay chưa (không phân biệt chữ hoa chữ thường)
    boolean existsByCategoryNameIgnoreCase(String name);
    boolean existsByCategoryNameIgnoreCaseAndCategoryIdNot(String name, Long id);
}