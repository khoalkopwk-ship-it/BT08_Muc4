package vn.iotstar.service;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HexFormat;

@Service
// Dịch vụ OTP (One-Time Password) cho xác thực người dùng
public class OtpService {
    public static final long VALID_MILLIS = 5 * 60 * 1000L;
    public static final int MAX_ATTEMPTS = 5;
    private final SecureRandom random = new SecureRandom();

    // Tạo mã OTP ngẫu nhiên gồm 6 chữ số
    public String generate() {
        return String.valueOf(100000 + random.nextInt(900000));
    }

    // Băm OTP bằng thuật toán SHA-256 và trả về chuỗi hex
    public String hash(String otp) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(otp.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("Máy ảo Java không hỗ trợ SHA-256", exception);
        }
    }

    // Kiểm tra xem OTP đã nhập có khớp với hash dự kiến hay không
    public boolean matches(String rawOtp, String expectedHash) {
        if (rawOtp == null || expectedHash == null) return false;
        return MessageDigest.isEqual(
                hash(rawOtp.trim()).getBytes(StandardCharsets.UTF_8),
                expectedHash.getBytes(StandardCharsets.UTF_8));
    }

    // Kiểm tra xem OTP đã hết hạn hay chưa dựa trên thời gian tạo
    public boolean expired(Long createdAt) {
        return createdAt == null || System.currentTimeMillis() - createdAt > VALID_MILLIS;
    }
}