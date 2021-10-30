package com.amx.milk.service;

import com.amx.milk.entity.User;
import com.amx.milk.entity.UserInfo;

import java.util.List;

public interface UserService {

    UserInfo getUserByMessage(String username, String password);
    List<UserInfo> getUserList();
    List<UserInfo> getUserByPage(int  pageNo,int pageSize);
    int addUser(UserInfo userInfo);
    int updateUser(UserInfo userInfo);
    int deleteUser(UserInfo userInfo);
}
