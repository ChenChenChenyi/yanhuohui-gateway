package com.chenyi.yanhuohui.dto;

import lombok.Data;

@Data
public class BaseResponse {
    private int code;
    private String message;
    private String data;

    public BaseResponse() {

    }

    public BaseResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public BaseResponse(int code, String message, String data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
}
