package com.banco.cuenta.dto;
import lombok.*; import java.time.LocalDateTime;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ApiResponse<T> {
    private boolean success; private String message; private T data;
    @Builder.Default private LocalDateTime timestamp = LocalDateTime.now();
    public static <T> ApiResponse<T> success(String msg, T data) { return ApiResponse.<T>builder().success(true).message(msg).data(data).build(); }
    public static <T> ApiResponse<T> success(T data) { return success("Operación exitosa", data); }
    public static <T> ApiResponse<T> error(String msg) { return ApiResponse.<T>builder().success(false).message(msg).build(); }
}
