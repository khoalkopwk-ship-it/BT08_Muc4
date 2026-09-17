package vn.iotstar.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import vn.iotstar.dto.request.ProductRequest;
import vn.iotstar.dto.response.PageResponse;
import vn.iotstar.dto.response.ProductResponse;
import vn.iotstar.entity.Product;

import java.util.List;
import java.util.Optional;

// Giao diện dịch vụ cho sản phẩm
public interface IProductService {
    Page<Product> findAll(String keyword, Pageable pageable);
    List<Product> findTop10Newest();
    Optional<Product> findById(Long id);
    Product save(Product product, Long categoryId, MultipartFile imageFile);
    void deleteById(Long id);

    // Các phương thức DTO dành cho REST API/AJAX.
    PageResponse<ProductResponse> findAllDto(String keyword, int page, int size);
    ProductResponse findDtoById(Long id);
    ProductResponse create(ProductRequest request);
    ProductResponse update(Long id, ProductRequest request);
    void deleteForApi(Long id);
}