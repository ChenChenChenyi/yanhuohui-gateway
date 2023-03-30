package com.chenyi.yanhuohui.dto;

import lombok.Data;

@Data
public class BaseResponse {
    private String code;
    private String message;
    private String data;

    public BaseResponse() {

    }

    public BaseResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public BaseResponse(String code, String message, String data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
}
