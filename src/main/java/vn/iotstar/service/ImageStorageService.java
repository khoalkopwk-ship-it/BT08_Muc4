package vn.iotstar.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.*;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
// Dịch vụ lưu trữ hình ảnh
public class ImageStorageService {
    private static final Set<String> ALLOWED =
            Set.of(".jpg", ".jpeg", ".png", ".gif", ".webp");
    private final Path uploadRoot;

    // Khởi tạo dịch vụ lưu trữ hình ảnh với thư mục upload được cấu hình
    public ImageStorageService(@Value("${app.upload-dir}") String uploadDir) {
        this.uploadRoot = Path.of(uploadDir).toAbsolutePath().normalize();
    }

    // Lưu tệp hình ảnh vào thư mục upload và trả về đường dẫn tương đối
    public String store(MultipartFile file, String folder) {
        if (file == null || file.isEmpty()) return null;

        String contentType = file.getContentType();
        if (contentType == null || !contentType.toLowerCase(Locale.ROOT).startsWith("image/")) {
            throw new IllegalArgumentException("Tệp được chọn không phải hình ảnh");
        }

        String original = file.getOriginalFilename() == null ? "" : file.getOriginalFilename();
        int dot = original.lastIndexOf('.');
        String extension = dot >= 0 ? original.substring(dot).toLowerCase(Locale.ROOT) : "";
        if (!ALLOWED.contains(extension)) {
            throw new IllegalArgumentException("Chỉ chấp nhận JPG, JPEG, PNG, GIF hoặc WEBP");
        }

        // Tạo thư mục upload nếu chưa tồn tại và lưu tệp hình ảnh với tên ngẫu nhiên
        try {
            Path directory = uploadRoot.resolve(folder).normalize();
            if (!directory.startsWith(uploadRoot)) {
                throw new IllegalArgumentException("Thư mục upload không hợp lệ");
            }
            Files.createDirectories(directory);
            String fileName = UUID.randomUUID() + extension;
            Path destination = directory.resolve(fileName).normalize();
            if (!destination.startsWith(directory)) {
                throw new IllegalArgumentException("Tên tệp không hợp lệ");
            }
            file.transferTo(destination);
            return "uploads/" + folder + "/" + fileName;
        } catch (IOException exception) {
            throw new UncheckedIOException("Không thể lưu ảnh", exception);
        }
    }
}