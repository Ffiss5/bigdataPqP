package com.itfan;

import com.itfan.mapper.UserMapper;
import com.itfan.pojo.User;
import com.itfan.pojo.IndexInfo;
import com.itfan.service.IndexService;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.security.Key;
@Slf4j
@SpringBootTest
class BigdataPqPApplicationTests {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private IndexService indaxService;

    @Test
    void contextLoads() {
    }

    @Test
    public void testIsnsert(){
        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        String base64Key= Encoders.BASE64.encode(key.getEncoded());
        System.out.println("bases64密钥"+base64Key);
    }
    public void testInsert(){

    }
    @Test
    public void testDate(){
        User user = new User(2,"rootfan",null,null,null,null,null);
//        User searchUser = userMapper.selectUserNameHeadImgEmail(user);
//        if(searchUser.getId()==null) searchUser.setId(4);
        log.info("用户信息"+user);
        IndexInfo backUserInfo = indaxService.userDaysInfo(user);
        log.info("查询的用户信息"+backUserInfo);
    }
}
