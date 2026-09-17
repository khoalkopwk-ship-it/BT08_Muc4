package vn.iotstar.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class ApiResponse<T> {
    private final boolean success;
    private final String message;
    private final T data;
    private final Map<String, String> errors;

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data, null);
    }

    public static ApiResponse<Void> failure(String message) {
        return new ApiResponse<>(false, message, null, null);
    }

    public static ApiResponse<Void> validation(
            String message,
            Map<String, String> errors) {
        return new ApiResponse<>(false, message, null, errors);
    }
}
