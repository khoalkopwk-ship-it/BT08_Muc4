package vn.iotstar.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.media.Schema;



@Getter
@Setter
@NoArgsConstructor
@Schema(
        description = "File icon của danh mục",
        type = "string",
        format = "binary"
    )
public class CategoryRequest {

    @NotBlank(message = "Tên danh mục không được để trống")
    @Size(max = 100, message = "Tên danh mục tối đa 100 ký tự")
    private String categoryName;

    @NotNull(message = "Trạng thái không được để trống")
    @Min(value = 0, message = "Trạng thái chỉ nhận giá trị 0 hoặc 1")
    @Max(value = 1, message = "Trạng thái chỉ nhận giá trị 0 hoặc 1")
    private Integer status = 1;

    private MultipartFile iconFile;

    
  
}
