package com.amx.milk.dao;

import com.amx.milk.entity.User;
import com.amx.milk.entity.UserInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Repository
public interface UserDao {

    UserInfo getUserByMessage(@Param("username") String username, @Param("password") String password);
    List<UserInfo> getUserList();
    List<UserInfo> getUserByPage(@RequestParam("page") int pageNo,@RequestParam("limit") int pageSize);
    int addUser(UserInfo userInfo);
    int updateUser(UserInfo userInfo);
    int deleteUser(UserInfo userInfo);
}
