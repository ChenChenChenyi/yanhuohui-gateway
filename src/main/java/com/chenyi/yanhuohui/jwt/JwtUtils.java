package com.chenyi.yanhuohui.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class JwtUtils {
    public static final String SECRET_KEY = "erbadagang-1899154"; //秘钥
    public static final long TOKEN_EXPIRE_TIME = 5 * 60 * 60 * 1000; //token过期时间
    public static final long REFRESH_TOKEN_EXPIRE_TIME = 10 * 60 * 1000; //refreshToken过期时间
    private static final String ISSUER = "yanhuohui"; //签发人

    /**
     * 生成签名
     */
    public static String generateToken(String username, String role) {
        Date now = new Date();
        Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY); //算法
        //设置头信息
        HashMap<String, Object> header = new HashMap<>(2);
        header.put("type", "JWT");
        header.put("alg", "HS256");

        String token = JWT.create()
                .withIssuer(ISSUER) //签发人
                .withIssuedAt(now) //签发时间
//                .withSubject()
                .withHeader(header)
                .withExpiresAt(new Date(now.getTime() + TOKEN_EXPIRE_TIME)) //过期时间
                .withClaim("username", username) //保存身份标识
                .withClaim("role", role) //保存权限标识
                .sign(algorithm);
        return token;
    }

    /**
     * 验证token
     */
    public static boolean verify(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY); //算法
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(ISSUER)
                    .build();
            DecodedJWT jwt = verifier.verify(token);//如果校验有问题会抛出异常。
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public static boolean checkToken(String jwtToken) {
        //TODO 根据自己的业务修改
        Map<String, Claim> claimsMap = parseJwt(jwtToken);
        /*
            对jwt里面的用户信息做判断
            根据自己的业务编写
         */
        String userName = claimsMap.get("username").asString();
        String role = claimsMap.get("role").asString();

        return true;
    }


    /**
     * 解析jwt
     */
    public static Map<String, Claim> parseJwt(String token) {
        Map<String, Claim> claims = null;
        Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT jwt = verifier.verify(token);
        claims = jwt.getClaims();
        return claims;
    }
}
