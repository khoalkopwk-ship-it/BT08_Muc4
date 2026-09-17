package vn.iotstar.controllers.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import vn.iotstar.dto.request.CategoryRequest;
import vn.iotstar.dto.response.ApiResponse;
import vn.iotstar.dto.response.CategoryResponse;
import vn.iotstar.dto.response.PageResponse;
import vn.iotstar.service.ICategoryService;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(
        value = "/api/categories",
        produces = MediaType.APPLICATION_JSON_VALUE
)
@Tag(
        name = "Category",
        description = "RESTful API quản lý danh mục sản phẩm"
)
public class CategoryRestController {

    private final ICategoryService categoryService;

    /**
     * Lấy danh sách Category có tìm kiếm và phân trang.
     *
     * Ví dụ:
     * GET /api/categories
     * GET /api/categories?keyword=áo&page=0&size=5
     */
    @Operation(
            summary = "Tìm kiếm và phân trang danh mục",
            description = """
                    Trả về danh sách danh mục theo từ khóa và phân trang.

                    Quy ước:
                    - page bắt đầu từ 0.
                    - size nằm trong khoảng từ 1 đến 100.
                    - keyword rỗng sẽ trả về toàn bộ danh mục.
                    """
    )
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<CategoryResponse>>> findAll(
            @Parameter(
                    description = "Từ khóa tìm kiếm theo tên danh mục",
                    example = "Áo"
            )
            @RequestParam(
                    name = "keyword",
                    defaultValue = ""
            )
            String keyword,

            @Parameter(
                    description = "Số trang, bắt đầu từ 0",
                    example = "0"
            )
            @RequestParam(
                    name = "page",
                    defaultValue = "0"
            )
            @Min(
                    value = 0,
                    message = "Số trang phải lớn hơn hoặc bằng 0"
            )
            int page,

            @Parameter(
                    description = "Số phần tử trên một trang, từ 1 đến 100",
                    example = "5"
            )
            @RequestParam(
                    name = "size",
                    defaultValue = "5"
            )
            @Min(
                    value = 1,
                    message = "Kích thước trang phải lớn hơn hoặc bằng 1"
            )
            @Max(
                    value = 100,
                    message = "Kích thước trang không được lớn hơn 100"
            )
            int size) {

        PageResponse<CategoryResponse> result =
                categoryService.findAllDto(keyword, page, size);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Lấy danh sách danh mục thành công",
                        result
                )
        );
    }

    /**
     * Lấy các Category đang hoạt động.
     *
     * API này được sử dụng để đưa dữ liệu Category
     * vào select box của form Product.
     */
    @Operation(
            summary = "Lấy danh mục đang hoạt động",
            description = """
                    Trả về những danh mục có trạng thái hoạt động.

                    API này thường được AJAX gọi để hiển thị danh sách
                    danh mục trong combobox của form thêm hoặc sửa Product.
                    """
    )
    @GetMapping("/options")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> findActiveOptions() {

        List<CategoryResponse> categories =
                categoryService.findActiveDto();

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Lấy danh mục đang hoạt động thành công",
                        categories
                )
        );
    }

    /**
     * Lấy chi tiết một Category theo ID.
     */
    @Operation(
            summary = "Lấy chi tiết danh mục",
            description = "Trả về thông tin của một danh mục theo mã danh mục."
    )
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> findById(
            @Parameter(
                    description = "Mã danh mục cần lấy thông tin",
                    required = true,
                    example = "1"
            )
            @PathVariable("id")
            Long id) {

        CategoryResponse category =
                categoryService.findDtoById(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Lấy thông tin danh mục thành công",
                        category
                )
        );
    }

    /**
     * Thêm Category.
     *
     * Dữ liệu được gửi dưới dạng multipart/form-data vì có upload icon.
     */
    @Operation(
            summary = "Thêm danh mục",
            description = """
                    Thêm một danh mục mới.

                    Request sử dụng multipart/form-data gồm:
                    - categoryName: tên danh mục.
                    - status: trạng thái 0 hoặc 1.
                    - iconFile: file ảnh đại diện của danh mục.
                    """
    )
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<CategoryResponse>> create(
            @Valid
            @ModelAttribute
            CategoryRequest request) {

        CategoryResponse createdCategory =
                categoryService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                "Thêm danh mục thành công",
                                createdCategory
                        )
                );
    }

    /**
     * Cập nhật Category.
     *
     * Nếu không chọn iconFile mới thì service giữ lại icon hiện tại.
     */
    @Operation(
            summary = "Cập nhật danh mục",
            description = """
                    Cập nhật thông tin danh mục theo ID.

                    Request sử dụng multipart/form-data gồm:
                    - categoryName: tên danh mục.
                    - status: trạng thái 0 hoặc 1.
                    - iconFile: ảnh mới, có thể không gửi nếu muốn giữ ảnh cũ.
                    """
    )
    @PutMapping(
            value = "/{id}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ApiResponse<CategoryResponse>> update(
            @Parameter(
                    description = "Mã danh mục cần cập nhật",
                    required = true,
                    example = "1"
            )
            @PathVariable("id")
            Long id,

            @Valid
            @ModelAttribute
            CategoryRequest request) {

        CategoryResponse updatedCategory =
                categoryService.update(id, request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Cập nhật danh mục thành công",
                        updatedCategory
                )
        );
    }

    /**
     * Xóa Category.
     */
    @Operation(
            summary = "Xóa danh mục",
            description = """
                    Xóa danh mục theo ID.

                    Nếu danh mục đang được Product tham chiếu,
                    service phải từ chối xóa và trả về thông báo phù hợp.
                    """
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @Parameter(
                    description = "Mã danh mục cần xóa",
                    required = true,
                    example = "1"
            )
            @PathVariable("id")
            Long id) {

        categoryService.deleteForApi(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Xóa danh mục thành công",
                        null
                )
        );
    }
}