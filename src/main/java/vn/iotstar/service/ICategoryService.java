package vn.iotstar.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import vn.iotstar.dto.request.CategoryRequest;
import vn.iotstar.dto.response.CategoryResponse;
import vn.iotstar.dto.response.PageResponse;
import vn.iotstar.entity.Category;

import java.util.List;
import java.util.Optional;

// Giao diện dịch vụ cho danh mục sản phẩm
public interface ICategoryService {
    Page<Category> findAll(String keyword, Pageable pageable);
    List<Category> findActive();
    Optional<Category> findById(Long id);
    Category save(Category category, MultipartFile iconFile);
    void deleteById(Long id);
    boolean isDuplicateName(Category category);

    // Các phương thức DTO dành cho REST API/AJAX.
    PageResponse<CategoryResponse> findAllDto(String keyword, int page, int size);
    List<CategoryResponse> findActiveDto();
    CategoryResponse findDtoById(Long id);
    CategoryResponse create(CategoryRequest request);
    CategoryResponse update(Long id, CategoryRequest request);
    void deleteForApi(Long id);


    
}