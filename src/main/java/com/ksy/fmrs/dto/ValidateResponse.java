package com.ksy.fmrs.dto;

public record ValidateResponse<T>(
        T dto, boolean isValid, String message
) {

    public static <T> ValidateResponse<T> success(T dto){
        return new ValidateResponse<T>(dto, true, "Validate Success");
    }

    public static <T> ValidateResponse<T> invalid(T dto, String message){
        return new ValidateResponse<T>(dto, false, message);
    }
}
