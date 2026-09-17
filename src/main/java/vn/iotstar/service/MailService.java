package vn.iotstar.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
// Dịch vụ gửi email, đặc biệt là gửi mã OTP
// Sử dụng JavaMailSender để gửi email
public class MailService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String sender;

    public void sendOtp(String receiver, String otp, String purpose) {
        if (sender == null || sender.isBlank()) {
            throw new IllegalStateException("Chưa cấu hình MAIL_USERNAME và MAIL_PASSWORD");
        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(sender);
        message.setTo(receiver);
        message.setSubject("Mã OTP " + purpose);
        message.setText("Mã OTP của bạn là: " + otp
                + "\nMã có hiệu lực trong 5 phút. Không chia sẻ mã này.");
        mailSender.send(message);
    }
}