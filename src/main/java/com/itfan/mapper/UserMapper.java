package com.itfan.mapper;

import com.itfan.pojo.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    /**
     * 查询用户信息
     * @param user
     * @return
     */
    @Select("select id,username,password from ds_mh.user_info where username=#{username}")
    User selectUserByUsername(User user);

    /**
     * 插入用户信息
     */
    @Insert("insert into ds_mh.user_info (username,password) values (#{username},#{password})")
    void insertUserNamePassword(User user);
    /**
     * 登录界面查询用户的名字和头像,邮箱等信息
     */
    @Select("select name,img_head,email from ds_mh.user_info where id=#{id} and username=#{username}")
    User selectUserNameHeadImgEmail(User user);
}
