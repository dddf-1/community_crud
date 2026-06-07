package com.example.community.global;

public class ApiResponse<T> {

    // 요청 처리 결과 메시지
    private String message;

    // 실제 응답 데이터
    // 실패 응답일 때는 null로 내려간다.
    private T data;

    public ApiResponse(String message, T data) {
        this.message = message;
        this.data = data;
    }
    public static <T> ApiResponse<T> success(String message, T data) {

        // 성공 응답을 만들 때 사용
        return new ApiResponse<>(message, data);
    }
    public static <T> ApiResponse<T> fail(String message) {

        // 실패 응답을 만들 때 사용
        return new ApiResponse<>(message, null);
    }
    public String getMessage() {
        return message;
    }
    public T getData() {
        return data;
    }
}