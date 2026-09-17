package vn.iotstar.controllers.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;


// Controller quản lý danh mục sản phẩm trong trang quản trị
// Cung cấp các phương thức để hiển thị danh sách danh mục, tạo mới, chỉnh sửa và xóa danh mục
// thymeleaf/admin/category/list: hiển thị danh sách danh mục
// thymeleaf/admin/category/form: hiển thị form tạo mới hoặc chỉnh sửa danh mục
@Controller
@RequestMapping("/admin/categories")
public class CategoryController {
    @GetMapping
    public String showCategoryPage() {
        return "thymeleaf/admin/categories";
    }

    @ModelAttribute("activeMenu")
    public String activeMenu() {
        return "category";
    }

}
