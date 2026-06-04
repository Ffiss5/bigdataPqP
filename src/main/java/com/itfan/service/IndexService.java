package com.itfan.service;

import com.itfan.pojo.User;
import com.itfan.pojo.IndexInfo;


public interface IndexService {
    /**
     * 返回一个倒计时时间
     */
    IndexInfo userDaysInfo(User user);
}
