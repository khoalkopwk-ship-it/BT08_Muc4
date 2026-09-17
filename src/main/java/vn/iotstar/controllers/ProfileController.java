package vn.iotstar.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.iotstar.dto.ProfileForm;
import vn.iotstar.entity.User;
import vn.iotstar.service.IUserService;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
// Bộ điều khiển cho trang hồ sơ người dùng
// Cung cấp các phương thức để hiển thị và cập nhật thông tin hồ sơ người dùng
// Cung cấp các phương thức để xử lý các yêu cầu GET và POST cho trang hồ sơ
// Cung cấp các phương thức để xử lý các lỗi xác thực và hiển thị thông báo thành công
// Cung cấp các phương thức để xử lý các tệp hình ảnh tải lên và lưu trữ chúng
// Cung cấp các phương thức để lấy thông tin người dùng hiện tại từ đối tượng Principal
// Cung cấp các phương thức để chuyển hướng người dùng sau khi cập nhật hồ sơ thành công
// Cung cấp các phương thức để hiển thị thông tin hồ sơ người dùng trong mô hình Model
// Cung cấp các phương thức để xác thực dữ liệu đầu vào từ biểu mẫu ProfileForm
// Cung cấp các phương thức để xử lý các ngoại lệ IllegalArgumentException và IllegalStateException
public class ProfileController {
    private final IUserService userService;

    @GetMapping("/profile")
    public String profile(Principal principal, Model model) {
        User user = userService.findByUsername(principal.getName()).orElseThrow();
        ProfileForm form = new ProfileForm();
        form.setFullname(user.getFullname());
        form.setPhone(user.getPhone() == null ? "" : user.getPhone());
        model.addAttribute("profileForm", form);
        model.addAttribute("profile", user);
        return "web/profile";
    }

    @PostMapping("/profile")
    public String update(@Valid @ModelAttribute("profileForm") ProfileForm form,
                         BindingResult errors,
                         @RequestParam(name = "image", required = false) MultipartFile image,
                         Principal principal,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (errors.hasErrors()) {
            model.addAttribute("profile",
                    userService.findByUsername(principal.getName()).orElseThrow());
            return "web/profile";
        }
        try {
            userService.updateProfile(principal.getName(), form.getFullname(), form.getPhone(), image);
            redirectAttributes.addFlashAttribute("success", "Cập nhật hồ sơ thành công");
            return "redirect:/profile";
        } catch (IllegalArgumentException | IllegalStateException exception) {
            errors.reject("profile", exception.getMessage());
            model.addAttribute("profile",
                    userService.findByUsername(principal.getName()).orElseThrow());
            return "web/profile";
        }
    }
}