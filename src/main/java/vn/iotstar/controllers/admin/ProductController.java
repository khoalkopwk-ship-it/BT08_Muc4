package vn.iotstar.controllers.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

// Bộ điều khiển cho quản lý sản phẩm trong trang quản trị
// Cung cấp các phương thức để hiển thị danh sách sản phẩm, tạo mới,
// chỉnh sửa và xóa sản phẩm
@Controller
@RequestMapping("/admin/products")
public class ProductController {
    @GetMapping
    public String showProductPage() {
        return "thymeleaf/admin/products";
    }

    @ModelAttribute("activeMenu")
    public String activeMenu() {
        return "product";
    }
}
