package vn.iotstar.controllers.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
        name = "Security",
        description = "API hỗ trợ kiểm thử CSRF trên Swagger UI"
)
@RestController
@RequestMapping("/api")
public class CsrfRestController {

    @Operation(
            summary = "Lấy CSRF token",
            description = """
                    Gọi API này trước khi kiểm thử POST, PUT hoặc DELETE
                    trong Swagger UI.
                    """
    )
    @GetMapping("/csrf")
    public CsrfToken csrf(CsrfToken token) {
        return token;
    }
}