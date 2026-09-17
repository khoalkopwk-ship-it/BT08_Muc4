package vn.iotstar.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vn.iotstar.dto.request.CategoryRequest;
import vn.iotstar.dto.response.CategoryResponse;
import vn.iotstar.dto.response.PageResponse;
import vn.iotstar.entity.Category;
import vn.iotstar.exception.BusinessException;
import vn.iotstar.exception.DuplicateResourceException;
import vn.iotstar.exception.ResourceNotFoundException;
import vn.iotstar.mapper.CategoryMapper;
import vn.iotstar.repository.CategoryRepository;
import vn.iotstar.repository.ProductRepository;
import vn.iotstar.service.ICategoryService;
import vn.iotstar.service.ImageStorageService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
// Dịch vụ triển khai cho danh mục sản phẩm
// Giao diện dịch vụ triển khai cho danh mục sản phẩm
// Cung cấp các phương thức để quản lý danh mục sản phẩm, bao gồm tìm kiếm, lưu trữ và xóa danh mục
public class CategoryServiceImpl implements ICategoryService {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ImageStorageService imageStorageService;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<Category> findAll(String keyword, Pageable pageable) {
        if (keyword == null || keyword.isBlank()) return categoryRepository.findAll(pageable);
        return categoryRepository.findByCategoryNameContainingIgnoreCase(keyword.trim(), pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> findActive() {
        return categoryRepository.findByStatusOrderByCategoryNameAsc(1);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Category> findById(Long id) {
        return categoryRepository.findById(id);
    }

    @Override
    public Category save(Category input, MultipartFile iconFile) {
        Category target;
        if (input.getCategoryId() == null) {
            target = new Category();
        } else {
            target = categoryRepository.findById(input.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy danh mục"));
        }

        target.setCategoryName(input.getCategoryName().trim());
        target.setStatus(input.getStatus());
        String uploaded = imageStorageService.store(iconFile, "categories");
        if (uploaded != null) target.setIcon(uploaded);
        return categoryRepository.save(target);
    }

    // Kiểm tra xem tên danh mục đã tồn tại hay chưa (không phân biệt chữ hoa chữ thường)
    @Override
    public void deleteById(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new IllegalArgumentException("Không tìm thấy danh mục");
        }
        if (productRepository.countByCategoryCategoryId(id) > 0) {
            throw new IllegalStateException("Không thể xóa danh mục đang có sản phẩm");
        }
        categoryRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isDuplicateName(Category category) {
        String name = category.getCategoryName().trim();
        if (category.getCategoryId() == null) {
            return categoryRepository.existsByCategoryNameIgnoreCase(name);
        }
        return categoryRepository.existsByCategoryNameIgnoreCaseAndCategoryIdNot(
                name, category.getCategoryId());
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CategoryResponse> findAllDto(
            String keyword,
            int page,
            int size) {
        Pageable pageable = createPageable(page, size);
        Page<Category> categories = findAll(keyword, pageable);
        return PageResponse.from(categories.map(categoryMapper::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> findActiveDto() {
        return categoryRepository.findByStatusOrderByCategoryNameAsc(1)
                .stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse findDtoById(Long id) {
        return categoryMapper.toResponse(findRequired(id));
    }

    @Override
    public CategoryResponse create(CategoryRequest request) {
        String normalizedName = request.getCategoryName().trim();
        if (categoryRepository.existsByCategoryNameIgnoreCase(normalizedName)) {
            throw new DuplicateResourceException("Tên danh mục đã tồn tại");
        }

        Category category = categoryMapper.toEntity(request);
        String uploaded = imageStorageService.store(
                request.getIconFile(), "categories");
        if (uploaded != null) category.setIcon(uploaded);

        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    @Override
    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = findRequired(id);
        String normalizedName = request.getCategoryName().trim();

        if (categoryRepository
                .existsByCategoryNameIgnoreCaseAndCategoryIdNot(normalizedName, id)) {
            throw new DuplicateResourceException("Tên danh mục đã tồn tại");
        }

        categoryMapper.updateEntity(request, category);
        String uploaded = imageStorageService.store(
                request.getIconFile(), "categories");
        if (uploaded != null) category.setIcon(uploaded);

        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    @Override
    public void deleteForApi(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Không tìm thấy danh mục có id: " + id);
        }
        if (productRepository.countByCategoryCategoryId(id) > 0) {
            throw new BusinessException(
                    "Không thể xóa danh mục đang có sản phẩm");
        }
        categoryRepository.deleteById(id);
    }

    private Category findRequired(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy danh mục có id: " + id));
    }

    private Pageable createPageable(int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 100);
        return PageRequest.of(
                safePage,
                safeSize,
                Sort.by("categoryId").descending());
    }
}
