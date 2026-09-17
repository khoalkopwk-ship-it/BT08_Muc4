package vn.iotstar.mapper;

import org.springframework.stereotype.Component;
import vn.iotstar.dto.request.ProductRequest;
import vn.iotstar.dto.response.ProductResponse;
import vn.iotstar.entity.Category;
import vn.iotstar.entity.Product;

@Component
public class ProductMapper {

    public ProductResponse toResponse(Product entity) {
        if (entity == null) return null;

        Category category = entity.getCategory();

        return ProductResponse.builder()
                .productId(entity.getProductId())
                .productName(entity.getProductName())
                .price(entity.getPrice())
                .description(entity.getDescription())
                .image(entity.getImage())
                .imageUrl(toPublicUrl(entity.getImage()))
                .createdDate(entity.getCreatedDate())
                .categoryId(category == null ? null : category.getCategoryId())
                .categoryName(category == null ? null : category.getCategoryName())
                .build();
    }

    public Product toEntity(ProductRequest request, Category category) {
        if (request == null) return null;

        Product entity = new Product();
        updateEntity(request, entity, category);
        return entity;
    }

    public void updateEntity(
            ProductRequest request,
            Product entity,
            Category category) {
        entity.setProductName(request.getProductName().trim());
        entity.setPrice(request.getPrice());
        entity.setDescription(clean(request.getDescription()));
        entity.setCategory(category);
    }

    private String clean(String value) {
        if (value == null) return null;
        String cleaned = value.trim();
        return cleaned.isEmpty() ? null : cleaned;
    }

    private String toPublicUrl(String storedPath) {
        if (storedPath == null || storedPath.isBlank()) return null;
        return storedPath.startsWith("/") ? storedPath : "/" + storedPath;
    }
}
