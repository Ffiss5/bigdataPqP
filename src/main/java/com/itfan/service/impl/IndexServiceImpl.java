package com.itfan.service.impl;

import com.itfan.mapper.UserMapper;
import com.itfan.pojo.User;
import com.itfan.pojo.IndexInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.time.Year;

@Slf4j
@Service
public class IndexServiceImpl implements com.itfan.service.IndexService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public IndexInfo userDaysInfo(User user) {
        log.info("传到服务这里的用户信息:"+user);
        int year = Year.now().plusYears(1).getValue();
        LocalDate today = LocalDate.now();
        LocalDate endTime = LocalDate.of(year, 4, 16);
        Period between = Period.between(today, endTime);
        int months = between.getMonths();
        int days = between.getDays();
        User searchUserInfo = userMapper.selectUserNameHeadImgEmail(user);
        log.info("查询后的用户信息:{}",searchUserInfo);
        return new IndexInfo(user.getId(), searchUserInfo.getName(), searchUserInfo.getImgHead(),searchUserInfo.getEmail(),months,days);
    }
}
