package com.amx.milk.service;

import com.amx.milk.dao.UserDao;
import com.amx.milk.entity.User;
import com.amx.milk.entity.UserInfo;
import com.github.pagehelper.PageHelper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserServiceImpl implements UserService{

    @Resource
    private UserDao userDao;

    @Override
    public UserInfo getUserByMessage(String username, String password) {
        return userDao.getUserByMessage(username,password);
    }

    @Override
    public List<UserInfo> getUserList() {
        return userDao.getUserList();
    }

    @Override
    public List<UserInfo> getUserByPage(int pageNo, int pageSize) {
        PageHelper.startPage(pageNo, pageSize);
        List<UserInfo> userList = userDao.getUserList();
        return userList;
    }

    @Override
    public int addUser(UserInfo userInfo) {
        return userDao.addUser(userInfo);
    }

    @Override
    public int updateUser(UserInfo userInfo) {
        return userDao.updateUser(userInfo);
    }

    @Override
    public int deleteUser(UserInfo userInfo) {
        return userDao.deleteUser(userInfo);
    }

}
