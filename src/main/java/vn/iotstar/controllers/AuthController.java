package vn.iotstar.controllers;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.iotstar.dto.PendingRegistration;
import vn.iotstar.dto.RegisterForm;
import vn.iotstar.service.IUserService;
import vn.iotstar.service.MailService;
import vn.iotstar.service.OtpService;

import java.util.Objects;

@Controller
@RequiredArgsConstructor
// Bộ điều khiển cho xác thực người dùng
// Cung cấp các phương thức để xử lý đăng ký, xác minh OTP, quên
public class AuthController {
    private static final String REG_PENDING = "registerPending";
    private static final String REG_HASH = "registerOtpHash";
    private static final String REG_TIME = "registerOtpTime";
    private static final String REG_ATTEMPTS = "registerOtpAttempts";
    private static final String FORGOT_EMAIL = "forgotEmail";
    private static final String FORGOT_HASH = "forgotOtpHash";
    private static final String FORGOT_TIME = "forgotOtpTime";
    private static final String FORGOT_ATTEMPTS = "forgotOtpAttempts";
    private static final String FORGOT_VERIFIED = "forgotVerified";

    private final IUserService userService;
    private final MailService mailService;
    private final OtpService otpService;
    private final PasswordEncoder passwordEncoder;

    // Cung cấp các phương thức để xử lý đăng ký, xác minh OTP, quên mật khẩu và đặt lại mật khẩu
    @GetMapping("/register")
    public String register(Model model) {
        if (!model.containsAttribute("registerForm")) {
            model.addAttribute("registerForm", new RegisterForm());
        }
        return "auth/register";
    }

    // Xử lý yêu cầu đăng ký người dùng mới
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registerForm") RegisterForm form,
                           BindingResult errors, HttpSession session) {
        if (!Objects.equals(form.getPassword(), form.getConfirmPassword())) {
            errors.rejectValue("confirmPassword", "mismatch", "Mật khẩu xác nhận không khớp");
        }
        if (!errors.hasFieldErrors("username") && userService.usernameExists(form.getUsername())) {
            errors.rejectValue("username", "duplicate", "Username đã tồn tại");
        }
        if (!errors.hasFieldErrors("email") && userService.emailExists(form.getEmail())) {
            errors.rejectValue("email", "duplicate", "Email đã được sử dụng");
        }
        if (errors.hasErrors()) return "auth/register";

        String otp = otpService.generate();
        try {
            mailService.sendOtp(form.getEmail().trim(), otp, "kích hoạt tài khoản");
        } catch (RuntimeException exception) {
            errors.reject("mail", "Không gửi được OTP. Kiểm tra cấu hình email");
            return "auth/register";
        }

        PendingRegistration pending = new PendingRegistration(
                form.getFullname().trim(), form.getUsername().trim(),
                form.getEmail().trim().toLowerCase(), form.getPhone().trim(),
                passwordEncoder.encode(form.getPassword()));
        session.setAttribute(REG_PENDING, pending);
        session.setAttribute(REG_HASH, otpService.hash(otp));
        session.setAttribute(REG_TIME, System.currentTimeMillis());
        session.setAttribute(REG_ATTEMPTS, 0);
        return "redirect:/verify-otp";
    }

    // Xử lý yêu cầu xác minh OTP cho đăng ký người dùng mới
    @GetMapping("/verify-otp")
    public String verifyRegister(HttpSession session) {
        return session.getAttribute(REG_PENDING) == null ? "redirect:/register" : "auth/verify-otp";
    }

    @PostMapping("/verify-otp")
    public String verifyRegister(@RequestParam String otp, HttpSession session,
                                 Model model, RedirectAttributes redirectAttributes) {
        PendingRegistration pending = (PendingRegistration) session.getAttribute(REG_PENDING);
        String hash = (String) session.getAttribute(REG_HASH);
        Long time = (Long) session.getAttribute(REG_TIME);
        if (pending == null || hash == null) return "redirect:/register";
        if (otpService.expired(time)) {
            clearRegister(session);
            redirectAttributes.addFlashAttribute("error", "OTP đã hết hạn. Vui lòng đăng ký lại");
            return "redirect:/register";
        }
        Object registerAttemptValue = session.getAttribute(REG_ATTEMPTS);
        int attempts = (registerAttemptValue instanceof Number number ? number.intValue() : 0) + 1;
        session.setAttribute(REG_ATTEMPTS, attempts);
        if (!otpService.matches(otp, hash)) {
            if (attempts >= OtpService.MAX_ATTEMPTS) {
                clearRegister(session);
                redirectAttributes.addFlashAttribute("error", "Bạn đã nhập sai quá 5 lần");
                return "redirect:/register";
            }
            model.addAttribute("error", "OTP không đúng. Còn "
                    + (OtpService.MAX_ATTEMPTS - attempts) + " lần thử");
            return "auth/verify-otp";
        }
        try {
            userService.registerVerified(pending);
        } catch (IllegalArgumentException exception) {
            clearRegister(session);
            redirectAttributes.addFlashAttribute("error", exception.getMessage());
            return "redirect:/register";
        }
        clearRegister(session);
        redirectAttributes.addFlashAttribute("success", "Đăng ký thành công. Hãy đăng nhập");
        return "redirect:/login";
    }

    // Xử lý yêu cầu quên mật khẩu
    @GetMapping("/forgot-password")
    public String forgotPassword() {
        return "auth/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestParam String email, HttpSession session, Model model) {
        String value = email == null ? "" : email.trim().toLowerCase();
        if (value.isBlank() || userService.findByEmail(value).isEmpty()) {
            model.addAttribute("error", "Email không tồn tại hoặc không hợp lệ");
            model.addAttribute("email", value);
            return "auth/forgot-password";
        }
        String otp = otpService.generate();
        try {
            mailService.sendOtp(value, otp, "đặt lại mật khẩu");
        } catch (RuntimeException exception) {
            model.addAttribute("error", "Không gửi được OTP. Kiểm tra cấu hình email");
            model.addAttribute("email", value);
            return "auth/forgot-password";
        }
        session.setAttribute(FORGOT_EMAIL, value);
        session.setAttribute(FORGOT_HASH, otpService.hash(otp));
        session.setAttribute(FORGOT_TIME, System.currentTimeMillis());
        session.setAttribute(FORGOT_ATTEMPTS, 0);
        session.removeAttribute(FORGOT_VERIFIED);
        return "redirect:/forgot-password/verify";
    }

    // Xử lý yêu cầu xác minh OTP cho quên mật khẩu
    @GetMapping("/forgot-password/verify")
    public String verifyForgot(HttpSession session) {
        return session.getAttribute(FORGOT_EMAIL) == null
                ? "redirect:/forgot-password" : "auth/verify-forgot-otp";
    }

    @PostMapping("/forgot-password/verify")
    public String verifyForgot(@RequestParam String otp, HttpSession session,
                               Model model, RedirectAttributes redirectAttributes) {
        String email = (String) session.getAttribute(FORGOT_EMAIL);
        String hash = (String) session.getAttribute(FORGOT_HASH);
        Long time = (Long) session.getAttribute(FORGOT_TIME);
        if (email == null || hash == null) return "redirect:/forgot-password";
        if (otpService.expired(time)) {
            clearForgot(session);
            redirectAttributes.addFlashAttribute("error", "OTP đã hết hạn");
            return "redirect:/forgot-password";
        }
        Object forgotAttemptValue = session.getAttribute(FORGOT_ATTEMPTS);
        int attempts = (forgotAttemptValue instanceof Number number ? number.intValue() : 0) + 1;
        session.setAttribute(FORGOT_ATTEMPTS, attempts);
        if (!otpService.matches(otp, hash)) {
            if (attempts >= OtpService.MAX_ATTEMPTS) {
                clearForgot(session);
                redirectAttributes.addFlashAttribute("error", "Bạn đã nhập sai quá 5 lần");
                return "redirect:/forgot-password";
            }
            model.addAttribute("error", "OTP không đúng. Còn "
                    + (OtpService.MAX_ATTEMPTS - attempts) + " lần thử");
            return "auth/verify-forgot-otp";
        }
        session.setAttribute(FORGOT_VERIFIED, true);
        session.removeAttribute(FORGOT_HASH);
        session.removeAttribute(FORGOT_TIME);
        session.removeAttribute(FORGOT_ATTEMPTS);
        return "redirect:/reset-password";
    }

    // Xử lý yêu cầu đặt lại mật khẩu
    @GetMapping("/reset-password")
    public String resetPassword(HttpSession session) {
        return Boolean.TRUE.equals(session.getAttribute(FORGOT_VERIFIED))
                ? "auth/reset-password" : "redirect:/forgot-password";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String password,
                                @RequestParam String confirmPassword,
                                HttpSession session, Model model,
                                RedirectAttributes redirectAttributes) {
        String email = (String) session.getAttribute(FORGOT_EMAIL);
        if (!Boolean.TRUE.equals(session.getAttribute(FORGOT_VERIFIED)) || email == null) {
            return "redirect:/forgot-password";
        }
        if (password == null || password.length() < 6 || password.length() > 100) {
            model.addAttribute("error", "Mật khẩu phải từ 6 đến 100 ký tự");
            return "auth/reset-password";
        }
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Mật khẩu xác nhận không khớp");
            return "auth/reset-password";
        }
        userService.resetPassword(email, password);
        clearForgot(session);
        redirectAttributes.addFlashAttribute("success", "Đổi mật khẩu thành công");
        return "redirect:/login";
    }

    // Xóa các thuộc tính liên quan đến đăng ký khỏi phiên làm việc
    private void clearRegister(HttpSession session) {
        session.removeAttribute(REG_PENDING);
        session.removeAttribute(REG_HASH);
        session.removeAttribute(REG_TIME);
        session.removeAttribute(REG_ATTEMPTS);
    }

    // Xóa các thuộc tính liên quan đến quên mật khẩu khỏi phiên làm việc
    private void clearForgot(HttpSession session) {
        session.removeAttribute(FORGOT_EMAIL);
        session.removeAttribute(FORGOT_HASH);
        session.removeAttribute(FORGOT_TIME);
        session.removeAttribute(FORGOT_ATTEMPTS);
        session.removeAttribute(FORGOT_VERIFIED);
    }
}