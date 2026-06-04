package com.itfan.controller;

import com.itfan.pojo.Result;
import com.itfan.pojo.User;
import com.itfan.pojo.IndexInfo;
import com.itfan.service.IndexService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@Slf4j
@RestController
@RequestMapping("/api")
public class IndexController {
    @Autowired
    private IndexService indaxService;
    @GetMapping("/test")
    public Result testport(){

        return Result.success("测试成功");
    }
    @GetMapping("/index")
    public Result getIndex(User user){
        log.info("Index get接口信息{}",user);
        if (user.getId()!=null && user.getUsername()!=null){
            IndexInfo usered = indaxService.userDaysInfo(user);
            return Result.success(usered);
        }
        return Result.Error("不要修路径");
    }
}
