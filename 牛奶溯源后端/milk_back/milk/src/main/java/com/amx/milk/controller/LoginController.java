package com.amx.milk.controller;

import com.amx.milk.entity.Response;
import com.amx.milk.entity.Token;
import com.amx.milk.entity.User;
import com.amx.milk.entity.UserInfo;
import com.amx.milk.service.UserServiceImpl;
import com.amx.milk.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
public class LoginController {

    @Autowired
    private UserServiceImpl userService;

    @PostMapping("/user/login")
    public Response login(@RequestBody User user){
        Response res = new Response();
        // user2为数据库中查到的数据
        UserInfo user2 = userService.getUserByMessage(user.getUsername(), user.getPassword());
        System.out.println(user2);
        if (user2 != null){
            // 如果该用户存在，则先创建Token
            String token = JwtUtil.createToken(user2.getUserName(),user2.getId()+"",user2.getAvatar(),user2.getIntroduction(),user2.getRole());
            res.setCode(200);
            res.setMessage("登录成功");
            res.setData(new Token(token));
        }else {
            res.setCode(500);
            res.setMessage("用户名或密码错误");
        }
        return res;
    }


    @GetMapping("/user/info")
    public Response getInfo(@RequestParam("token") String token){
        Response response = new Response();

        // 判断传进来的token是否有效

        if (JwtUtil.verifyToken(token)){
            String username = JwtUtil.getUserName(token);
            String userId = JwtUtil.getUserId(token);
            String userAvatar = JwtUtil.getUserAvatar(token);
            String userIntroduction = JwtUtil.getUserIntroduction(token);
            String userRole = JwtUtil.getUserRole(token);

            Map<String,Object> map = new HashMap<>();
            List<String> roles = new ArrayList<>();
            roles.add(userRole);
            map.put("roles",roles);
            map.put("avatar",userAvatar);
            map.put("introduction",userIntroduction);
            map.put("username",username);

            response.setMessage("加载信息成功");
            response.setCode(200);
            response.setData(map);
        }else {
            response.setCode(500);
            response.setMessage("加载信息失败");
        }
        return response;
    }


    // 退出登录，从请求头中获取X-Token参数 @RequestHeader
    @PostMapping("/user/logout")
    public Response logout(@RequestHeader("X-Token") String token){
        Response res = new Response();
        if (JwtUtil.verifyToken(token)){
            String username = JwtUtil.getUserName(token);
            res.setCode(200);
            res.setMessage("退出登录成功");

        }else {
            res.setCode(500);
            res.setMessage("退出登录失败");
        }
        return res;
    }
}
