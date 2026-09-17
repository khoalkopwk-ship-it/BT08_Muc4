package vn.iotstar.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.iotstar.entity.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Override
    // Sử dụng @EntityGraph để tải thông tin danh mục cùng với sản phẩm
    @EntityGraph(attributePaths = "category")
    Page<Product> findAll(Pageable pageable);

    @EntityGraph(attributePaths = "category")
    Page<Product> findByProductNameContainingIgnoreCase(String keyword, Pageable pageable);

    @EntityGraph(attributePaths = "category")
    List<Product> findTop10ByOrderByCreatedDateDescProductIdDesc();

    @EntityGraph(attributePaths = "category")
    Optional<Product> findWithCategoryByProductId(Long productId);

    long countByCategoryCategoryId(Long categoryId);

    boolean existsByProductNameIgnoreCase(String name);

    boolean existsByProductNameIgnoreCaseAndProductIdNot(String name, Long id);
}