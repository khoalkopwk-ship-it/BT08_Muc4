package vn.iotstar.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class ProductRequest {

    @Schema(description = "Tên sản phẩm", example = "Áo thun nam")
    @NotBlank(message = "Tên sản phẩm không được để trống")
    @Size(max = 255, message = "Tên sản phẩm tối đa 255 ký tự")
    private String productName;

    @Schema(description = "Giá bán, không âm", example = "250000")
    @NotNull(message = "Giá không được để trống")
    @DecimalMin(value = "0.0", inclusive = true, message = "Giá không được âm")
    @Digits(integer = 16, fraction = 2, message = "Giá không hợp lệ")
    private BigDecimal price;

    @Schema(description = "Mô tả sản phẩm", example = "Áo cotton form rộng")
    @Size(max = 2000, message = "Mô tả tối đa 2000 ký tự")
    private String description;

    @Schema(description = "Mã danh mục", example = "1")
    @NotNull(message = "Danh mục không được để trống")
    @Positive(message = "Mã danh mục phải lớn hơn 0")
    private Long categoryId;

    @Schema(description = "Ảnh sản phẩm (JPG, JPEG, PNG, GIF hoặc WEBP)", type = "string", format = "binary")
    private MultipartFile imageFile;
}
