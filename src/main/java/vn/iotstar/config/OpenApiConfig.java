package vn.iotstar.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI ecomartOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("EcoMart RESTful API")
                        .version("1.0.0")
                        .description("""
                                RESTful API quản lý Category và Product.

                                Chức năng:
                                - CRUD Category
                                - Tìm kiếm và phân trang Category
                                - CRUD Product
                                - Tìm kiếm và phân trang Product
                                - Upload hình ảnh
                                - Render dữ liệu bằng AJAX
                                """)
                        .contact(new Contact()
                                .name("Sinh viên thực hiện")
                                .email("student@hcmute.edu.vn"))
                        .license(new License()
                                .name("HCMUTE - Lập trình Web")));
    }

    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("ecomart-admin-api")
                .displayName("EcoMart Admin API")
                .pathsToMatch("/api/**")
                .build();
    }
}