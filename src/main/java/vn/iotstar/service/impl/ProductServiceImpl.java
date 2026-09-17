package vn.iotstar.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vn.iotstar.dto.request.ProductRequest;
import vn.iotstar.dto.response.PageResponse;
import vn.iotstar.dto.response.ProductResponse;
import vn.iotstar.entity.Category;
import vn.iotstar.entity.Product;
import vn.iotstar.exception.DuplicateResourceException;
import vn.iotstar.exception.ResourceNotFoundException;
import vn.iotstar.mapper.ProductMapper;
import vn.iotstar.repository.CategoryRepository;
import vn.iotstar.repository.ProductRepository;
import vn.iotstar.service.IProductService;
import vn.iotstar.service.ImageStorageService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
// Dịch vụ triển khai cho sản phẩm
// Giao diện dịch vụ triển khai cho sản phẩm
// Cung cấp các phương thức để quản lý sản phẩm, bao gồm tìm kiếm, lưu trữ và xóa sản phẩm
public class ProductServiceImpl implements IProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ImageStorageService imageStorageService;
    private final ProductMapper productMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<Product> findAll(String keyword, Pageable pageable) {
        if (keyword == null || keyword.isBlank()) return productRepository.findAll(pageable);
        return productRepository.findByProductNameContainingIgnoreCase(keyword.trim(), pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> findTop10Newest() {
        return productRepository.findTop10ByOrderByCreatedDateDescProductIdDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Product> findById(Long id) {
        return productRepository.findWithCategoryByProductId(id);
    }

    @Override
    public Product save(Product input, Long categoryId, MultipartFile imageFile) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Danh mục không tồn tại"));

        Product target;
        if (input.getProductId() == null) {
            target = new Product();
        } else {
            target = productRepository.findById(input.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm"));
        }

        target.setProductName(input.getProductName().trim());
        target.setPrice(input.getPrice());
        target.setDescription(input.getDescription() == null ? null : input.getDescription().trim());
        target.setCategory(category);

        String uploaded = imageStorageService.store(imageFile, "products");
        if (uploaded != null) target.setImage(uploaded);
        return productRepository.save(target);
    }

    @Override
    public void deleteById(Long id) {
        if (!productRepository.existsById(id)) {
            throw new IllegalArgumentException("Không tìm thấy sản phẩm");
        }
        productRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> findAllDto(
            String keyword,
            int page,
            int size) {
        Pageable pageable = createPageable(page, size);
        Page<Product> products = findAll(keyword, pageable);
        return PageResponse.from(products.map(productMapper::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse findDtoById(Long id) {
        return productMapper.toResponse(findRequired(id));
    }

    @Override
    public ProductResponse create(ProductRequest request) {
        String normalizedName = request.getProductName().trim();
        if (productRepository.existsByProductNameIgnoreCase(normalizedName)) {
            throw new DuplicateResourceException("Tên sản phẩm đã tồn tại");
        }

        Category category = findRequiredCategory(request.getCategoryId());
        Product product = productMapper.toEntity(request, category);

        String uploaded = imageStorageService.store(
                request.getImageFile(), "products");
        if (uploaded != null) product.setImage(uploaded);

        Product saved = productRepository.save(product);
        return productMapper.toResponse(saved);
    }

    @Override
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = findRequired(id);
        String normalizedName = request.getProductName().trim();

        if (productRepository
                .existsByProductNameIgnoreCaseAndProductIdNot(normalizedName, id)) {
            throw new DuplicateResourceException("Tên sản phẩm đã tồn tại");
        }

        Category category = findRequiredCategory(request.getCategoryId());
        productMapper.updateEntity(request, product, category);

        String uploaded = imageStorageService.store(
                request.getImageFile(), "products");
        if (uploaded != null) product.setImage(uploaded);

        Product saved = productRepository.save(product);
        return productMapper.toResponse(saved);
    }

    @Override
    public void deleteForApi(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Không tìm thấy sản phẩm có id: " + id);
        }
        productRepository.deleteById(id);
    }

    private Product findRequired(Long id) {
        return productRepository.findWithCategoryByProductId(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy sản phẩm có id: " + id));
    }

    private Category findRequiredCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy danh mục có id: " + categoryId));
    }

    private Pageable createPageable(int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 100);
        return PageRequest.of(
                safePage,
                safeSize,
                Sort.by("productId").descending());
    }
}
