package vn.iotstar.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long productId;
    private String productName;
    private BigDecimal price;
    private String description;
    private String image;
    private String imageUrl;
    private LocalDateTime createdDate;
    private Long categoryId;
    private String categoryName;
}
