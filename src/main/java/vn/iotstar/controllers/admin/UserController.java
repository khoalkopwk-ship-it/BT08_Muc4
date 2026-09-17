package vn.iotstar.controllers.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.iotstar.entity.User;
import vn.iotstar.service.IUserService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/users")
// Bộ điều khiển cho quản lý người dùng trong trang quản trị
// Cung cấp các phương thức để hiển thị danh sách người dùng, tạo mới,
// chỉnh sửa và xóa người dùng
public class UserController {
    private final IUserService userService;

    @GetMapping
    public String list(@RequestParam(defaultValue = "") String keyword,
                       @RequestParam(defaultValue = "0") int page,
                       Model model) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), 5,
                Sort.by("id").descending());
        model.addAttribute("result", userService.findAll(keyword, pageable));
        model.addAttribute("keyword", keyword);
        return "admin/user/list";
    }

    // Hiển thị biểu mẫu tạo người dùng mới
    @GetMapping("/new")
    public String create(Model model) {
        model.addAttribute("user", new User());
        return "admin/user/form";
    }

    // Hiển thị biểu mẫu chỉnh sửa người dùng hiện tại
    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model,
                       RedirectAttributes redirectAttributes) {
        User user = userService.findById(id).orElse(null);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy người dùng");
            return "redirect:/admin/users";
        }
        user.setPassword("");
        model.addAttribute("user", user);
        return "admin/user/form";
    }

    // Xử lý yêu cầu lưu người dùng mới hoặc cập nhật người dùng hiện tại
    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("user") User user,
                       BindingResult errors,
                       RedirectAttributes redirectAttributes) {
        if (user.getId() == null && (user.getPassword() == null || user.getPassword().isBlank())) {
            errors.rejectValue("password", "required", "Mật khẩu không được để trống");
        }
        if (!errors.hasFieldErrors("username") && userService.duplicateUsername(user)) {
            errors.rejectValue("username", "duplicate", "Username đã tồn tại");
        }
        if (!errors.hasFieldErrors("email") && userService.duplicateEmail(user)) {
            errors.rejectValue("email", "duplicate", "Email đã tồn tại");
        }
        if (errors.hasErrors()) return "admin/user/form";
        userService.save(user);
        redirectAttributes.addFlashAttribute("success", "Lưu người dùng thành công");
        return "redirect:/admin/users";
    }

    // Xử lý yêu cầu xóa người dùng theo ID
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Xóa người dùng thành công");
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("error", exception.getMessage());
        }
        return "redirect:/admin/users";
    }
}