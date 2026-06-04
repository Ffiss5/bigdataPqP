package com.itfan.service.impl;

import com.itfan.DTO.LoginRequest;
import com.itfan.mapper.UserMapper;
import com.itfan.pojo.Login;
import com.itfan.pojo.User;
import com.itfan.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class LoginServiceImpl implements com.itfan.service.LoginService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtils jwtUtils;
    @Override
    public Login login(LoginRequest request) {
        User incomingInfo = new User();
        incomingInfo.setUsername(request.getUsername());
        incomingInfo.setPassword(request.getPassword());
        User logInfo = userMapper.selectUserByUsername(incomingInfo);
       if (logInfo!=null && passwordEncoder.matches(incomingInfo.getPassword(),logInfo.getPassword())){
           log.info("登录成功,员工信息;{}",logInfo);
           Map<String, Object> claims=new HashMap<>();
           claims.put("userid",logInfo.getId());
           claims.put("role","USER");
           claims.put("sub",logInfo.getUsername());
           String token=jwtUtils.genertoToken(claims);
           return new Login(logInfo.getId(), logInfo.getUsername(), token);
       }
        return null;
    }

    @Override
    public Boolean register(User user_register_info) {
        User userinfo = userMapper.selectUserByUsername(user_register_info);
        if (userinfo == null && user_register_info.getPassword() != null) {
            User user = new User();
            user.setUsername(user_register_info.getUsername());
            user.setPassword(passwordEncoder.encode(user_register_info.getPassword()));
            userMapper.insertUserNamePassword(user);
            return true;
        }
        return false;
    }
}
