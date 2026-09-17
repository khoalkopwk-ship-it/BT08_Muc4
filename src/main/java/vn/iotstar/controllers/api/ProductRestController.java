package vn.iotstar.controllers.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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
import vn.iotstar.dto.request.ProductRequest;
import vn.iotstar.dto.response.ApiResponse;
import vn.iotstar.dto.response.PageResponse;
import vn.iotstar.dto.response.ProductResponse;
import vn.iotstar.service.IProductService;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(
        value = "/api/products",
        produces = MediaType.APPLICATION_JSON_VALUE
)
@Tag(name = "Products", description = "CRUD, tìm kiếm và phân trang sản phẩm")
public class ProductRestController {

    private final IProductService productService;

    /**
     * Tìm kiếm và phân trang Product.
     *
     * Ví dụ:
     * GET /api/products?keyword=áo&page=0&size=5
     */
    @GetMapping
    @Operation(summary = "Tìm kiếm và phân trang sản phẩm")
    public ResponseEntity<
            ApiResponse<PageResponse<ProductResponse>>
            > findAll(
            @RequestParam(
                    name = "keyword",
                    defaultValue = ""
            ) String keyword,

            @RequestParam(
                    name = "page",
                    defaultValue = "0"
            )
            @Min(
                    value = 0,
                    message = "Số trang không được âm"
            )
            int page,

            @RequestParam(
                    name = "size",
                    defaultValue = "5"
            )
            @Min(
                    value = 1,
                    message = "Kích thước trang phải lớn hơn 0"
            )
            @Max(
                    value = 100,
                    message = "Kích thước trang tối đa là 100"
            )
            int size) {

        PageResponse<ProductResponse> result =
                productService.findAllDto(keyword, page, size);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Lấy danh sách sản phẩm thành công",
                        result
                )
        );
    }

    /**
     * Lấy chi tiết Product theo ID.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Lấy chi tiết sản phẩm")
    public ResponseEntity<ApiResponse<ProductResponse>> findById(
            @Parameter(description = "Mã sản phẩm", example = "1")
            @PathVariable("id") @Positive(message = "Mã sản phẩm phải lớn hơn 0") Long id) {

        ProductResponse product =
                productService.findDtoById(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Lấy thông tin sản phẩm thành công",
                        product
                )
        );
    }

    /**
     * Thêm Product.
     *
     * Dữ liệu gửi lên dưới dạng multipart/form-data.
     */
    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @Operation(summary = "Thêm sản phẩm")
    public ResponseEntity<ApiResponse<ProductResponse>> create(
            @Valid @ModelAttribute ProductRequest request) {

        ProductResponse created =
                productService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                "Thêm sản phẩm thành công",
                                created
                        )
                );
    }

    /**
     * Cập nhật Product.
     *
     * Nếu imageFile không được chọn,
     * Service sẽ giữ lại ảnh hiện tại.
     */
    @PutMapping(
            value = "/{id}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @Operation(summary = "Cập nhật sản phẩm")
    public ResponseEntity<ApiResponse<ProductResponse>> update(
            @PathVariable("id") @Positive(message = "Mã sản phẩm phải lớn hơn 0") Long id,
            @Valid @ModelAttribute ProductRequest request) {

        ProductResponse updated =
                productService.update(id, request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Cập nhật sản phẩm thành công",
                        updated
                )
        );
    }

    /**
     * Xóa Product.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa sản phẩm")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable("id") @Positive(message = "Mã sản phẩm phải lớn hơn 0") Long id) {

        productService.deleteForApi(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Xóa sản phẩm thành công",
                        null
                )
        );
    }
}
