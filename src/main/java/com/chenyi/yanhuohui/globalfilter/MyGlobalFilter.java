package com.chenyi.yanhuohui.globalfilter;

import com.alibaba.fastjson.JSONObject;
import com.chenyi.yanhuohui.dto.BaseResponse;
import com.chenyi.yanhuohui.jwt.JWTUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
@Component
@ConfigurationProperties("org.my.jwt")
@Data
public class MyGlobalFilter implements GlobalFilter, Ordered {

    private String[] skipAuthUrls;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String url = exchange.getRequest().getURI().getPath();

        //跳过不需要验证的路径
        if (null != skipAuthUrls && isSkipUrl(url)) {
            return chain.filter(exchange);
        }

        //从请求头中取得token
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (StringUtils.isEmpty(token)) {
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.OK);
            response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");

            BaseResponse res = new BaseResponse(401, "401 unauthorized");
            //byte[] responseByte = JSONObject.fromObject(res).toString().getBytes(StandardCharsets.UTF_8);
            byte[] responseByte = JSONObject.toJSONBytes(res);

            DataBuffer buffer = response.bufferFactory().wrap(responseByte);
            return response.writeWith(Flux.just(buffer));
        }

        //请求中的token是否有效
        boolean verifyResult = JWTUtil.verify(token);
        if (!verifyResult) {
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.OK);
            response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");

            BaseResponse res = new BaseResponse(1004, "invalid token");
            //byte[] responseByte = JSONObject.fromObject(res).toString().getBytes(StandardCharsets.UTF_8);
            byte[] responseByte = JSONObject.toJSONBytes(res);
            DataBuffer buffer = response.bufferFactory().wrap(responseByte);
            return response.writeWith(Flux.just(buffer));
        } else {
            String roles = JWTUtil.getRoles(token);
            if (roles.indexOf("admin") >= 0) {//TODO 根据权限进行判断
                System.out.println("roles = " + roles);
            }
        }

        //如果各种判断都通过，执行chain上的其他业务逻辑
        return chain.filter(exchange);
    }


    @Override
    public int getOrder() {
        return -100;
    }

    public boolean isSkipUrl(String url) {
        for (String skipAuthUrl : skipAuthUrls) {
            if (url.startsWith(skipAuthUrl)) {
                return true;
            }
        }
        return false;
    }
}
