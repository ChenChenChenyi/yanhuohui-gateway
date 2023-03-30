package com.chenyi.yanhuohui.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.auth0.jwt.interfaces.Claim;
import com.chenyi.yanhuohui.constants.RequestKeyConstants;
import com.chenyi.yanhuohui.constants.ResultCode;
import com.chenyi.yanhuohui.dto.BaseResponse;
import com.chenyi.yanhuohui.jwt.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class JwtTokenFilter implements GlobalFilter, Ordered {
    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 校验token
     *
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String token = request.getHeaders().getFirst(RequestKeyConstants.TOKEN);
        //检查token是否为空
        if (StringUtils.isEmpty(token)) {
            return denyAccess(exchange, ResultCode.JWT_EXPIRED);
        }

//        if(JwtUtils.verify(token)){
//            return chain.filter(exchange);
//        }else {
//            return denyAccess(exchange, ResultCode.JWT_INVALID);
//        }
        return chain.filter(exchange);
    }

    /**
     * 拦截并返回自定义的json字符串
     */
    private Mono<Void> denyAccess(ServerWebExchange exchange, String resultCode) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.OK);
        //这里在返回头添加编码，否则中文会乱码
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setCode(resultCode);
        baseResponse.setMessage("empty token!");
        byte[] bytes = JSON.toJSONBytes(baseResponse, SerializerFeature.WriteMapNullValue);
        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
