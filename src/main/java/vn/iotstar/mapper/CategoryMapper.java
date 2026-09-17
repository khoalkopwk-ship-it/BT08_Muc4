package vn.iotstar.mapper;

import org.springframework.stereotype.Component;
import vn.iotstar.dto.request.CategoryRequest;
import vn.iotstar.dto.response.CategoryResponse;
import vn.iotstar.entity.Category;

@Component
public class CategoryMapper {

    public CategoryResponse toResponse(Category entity) {
        if (entity == null) return null;

        return CategoryResponse.builder()
                .categoryId(entity.getCategoryId())
                .categoryName(entity.getCategoryName())
                .icon(entity.getIcon())
                .iconUrl(toPublicUrl(entity.getIcon()))
                .status(entity.getStatus())
                .build();
    }

    public Category toEntity(CategoryRequest request) {
        if (request == null) return null;

        Category entity = new Category();
        updateEntity(request, entity);
        return entity;
    }

    public void updateEntity(CategoryRequest request, Category entity) {
        entity.setCategoryName(request.getCategoryName().trim());
        entity.setStatus(request.getStatus());
    }

    private String toPublicUrl(String storedPath) {
        if (storedPath == null || storedPath.isBlank()) return null;
        return storedPath.startsWith("/") ? storedPath : "/" + storedPath;
    }
}
