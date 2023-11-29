package com.chenyi.yanhuohui.controller;

import com.alibaba.fastjson.JSON;
import com.chenyi.yanhuohui.jwt.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试用的登录接口，应该放在customer服务
 */
@RestController
@Slf4j
@RequestMapping("/login")
public class LoginController {
    /**
     * 根据用户名和密码生成Token
     * @param name
     * @param password
     * @return
     */
    @RequestMapping("/login-by-password")
    public String authorization(@RequestParam String name, @RequestParam String password,@RequestParam String role) {
        //校验用户密码，目前空置

        //根据角色生成Token
        String token = JwtUtils.generateToken(name,role);
        log.info("## authorization name={}, token={}", name, token);
        return token;
    }

    /**
     * 校验Token
     * @param token
     * @return
     */
    @RequestMapping(value = {"/verify-token"}, method = RequestMethod.POST)
    public ResponseEntity<String> verifyToken(@RequestParam String token) {
        log.info("## verifyToken 参数 token={}", token);
        JwtUtils.checkToken(token);
        return new ResponseEntity<>(JSON.toJSONString("OK"), HttpStatus.OK);
    }
}
