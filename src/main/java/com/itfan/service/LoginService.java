package com.itfan.service;

import com.itfan.DTO.LoginRequest;
import com.itfan.pojo.Login;
import com.itfan.pojo.User;

public interface LoginService {
    /**
     * 登录接口
     * @param user
     * @return
     */
    Login login(LoginRequest user);

    /**
     *注册接口
     */
    Boolean register(User user);
}
