package com.itfan.controller;

import com.itfan.DTO.LoginRequest;
import com.itfan.pojo.Login;
import com.itfan.pojo.Result;
import com.itfan.pojo.User;
import com.itfan.service.LoginService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@Slf4j
@RestController
@RequestMapping("/api")
public class LoginController {
    @Autowired
    private LoginService loginService;

    /**
     * 登录接口
     * @param
     * @return
     */
    @PostMapping("/login")
    public Result login(@RequestBody @Valid LoginRequest request){
        log.info("登录: {}",request);
        Login info=loginService.login(request);
        if (info != null) {
            return Result.success(info);
        }
        return  Result.Error("用户名或密码错误");
    }

    /**
     * 注册接口
     * @param user
     * @return
     */
    @PostMapping("/login/register")
    public Result register(@RequestBody User user){
        log.info("注册: {}",user);
        Boolean TorF=loginService.register(user);
        if (TorF){
            return Result.success("注册成功!返回登录~");
        }
        return Result.Error("用户名重复");
    }
}
