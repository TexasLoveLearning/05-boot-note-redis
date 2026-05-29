package com.fyc._4bootnote.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fyc._4bootnote.common.Result;
import com.fyc._4bootnote.dto.UserLoginDTO;
import com.fyc._4bootnote.dto.UserRegisterDTO;
import com.fyc._4bootnote.entity.User;
import com.fyc._4bootnote.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @PostMapping("/register")
    public Result<String> register(@RequestBody UserRegisterDTO dto) {
        // 检查用户名是否已存在
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, dto.getUsername());
        if (userMapper.selectOne(wrapper) != null) {
            return Result.error("用户名已存在");
        }

        // 密码加密
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(encoder.encode(dto.getPassword()));
        userMapper.insert(user);

        return Result.success("注册成功");
    }

    @PostMapping("/login")
    public Result<Long> login(@RequestBody UserLoginDTO dto) {
        // 查用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, dto.getUsername());
        User user = userMapper.selectOne(wrapper);

        if (user == null) {
            return Result.error("用户名不存在");
        }

        // 比对密码
        if (!encoder.matches(dto.getPassword(), user.getPassword())) {
            return Result.error("密码错误");
        }

        return Result.success(user.getId());
    }
}
