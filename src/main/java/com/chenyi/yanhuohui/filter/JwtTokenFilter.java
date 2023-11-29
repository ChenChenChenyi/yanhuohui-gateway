package com.chenyi.yanhuohui.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.chenyi.yanhuohui.constants.RequestKeyConstants;
import com.chenyi.yanhuohui.constants.ResultCode;
import com.chenyi.yanhuohui.dto.GatewayBaseResponse;
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

import java.util.Map;

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
            GatewayBaseResponse gatewayBaseResponse = new GatewayBaseResponse();
            gatewayBaseResponse.setCode(ResultCode.JWT_EMPTY);
            gatewayBaseResponse.setMessage("Token为空！");
            return denyAccess(exchange, gatewayBaseResponse);
        } else {
            //有token
            try {
                jwtUtils.checkToken(token);
                return chain.filter(exchange);
            } catch (SignatureVerificationException e) {
                log.error(e.getMessage(), e);
                GatewayBaseResponse gatewayBaseResponse = new GatewayBaseResponse();
                gatewayBaseResponse.setCode(ResultCode.JWT_ERROR);
                gatewayBaseResponse.setMessage("验证Token失败！");
                return denyAccess(exchange, gatewayBaseResponse);
            } catch (TokenExpiredException e){
                log.error(e.getMessage(), e);
                GatewayBaseResponse gatewayBaseResponse = new GatewayBaseResponse();
                gatewayBaseResponse.setCode(ResultCode.JWT_EXPIRED);
                gatewayBaseResponse.setMessage("Token已失效！");
                return denyAccess(exchange, gatewayBaseResponse);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                GatewayBaseResponse gatewayBaseResponse = new GatewayBaseResponse();
                gatewayBaseResponse.setCode(ResultCode.JWT_ERROR);
                gatewayBaseResponse.setMessage("验证Token失败！");
                return denyAccess(exchange, gatewayBaseResponse);
            }
        }


        Map claimMap = jwtUtils.parseJwt(token);
        //token有误
        if (claimMap.containsKey("exception")) {
            log.error() (claimMap.get("exception").toString());
            return denyAccess(exchange, ResultCode.TOKEN_INVALID);
        }

        //token无误，将用户信息设置进header中,传递到下游服务
        String userId = claimMap.get("").asString();
        Consumer<HttpHeaders> headers = httpHeaders -> {
            httpHeaders.add(RequestKeyConstants.USER_ID, userId);
        };
        request.mutate().headers(headers).build()


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
    private Mono<Void> denyAccess(ServerWebExchange exchange, GatewayBaseResponse gatewayBaseResponse) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        //这里在返回头添加编码，否则中文会乱码
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        byte[] bytes = JSON.toJSONBytes(gatewayBaseResponse, SerializerFeature.WriteMapNullValue);
        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
