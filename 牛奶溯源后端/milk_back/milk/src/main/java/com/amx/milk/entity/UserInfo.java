package com.amx.milk.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserInfo implements Serializable {

    private int id;  // 用户的ID
    private String userName;   // 用户名
    private String passWord;   // 用户密码
    private String role;       // 用户角色
    private String avatar;     // 用户头像
    private String introduction;  // 用户描述

}
