package com.chenyi.yanhuohui.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class GatewayBaseResponse {
    private String code;
    private String message;
    private String data;

    public GatewayBaseResponse() {

    }

    public GatewayBaseResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public GatewayBaseResponse(String code, String message, String data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
}
