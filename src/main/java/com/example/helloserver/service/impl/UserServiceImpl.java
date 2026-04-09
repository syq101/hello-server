package com.example.helloserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.helloserver.common.Result;
import com.example.helloserver.common.ResultCode;
import com.example.helloserver.dto.UserDTO;
import com.example.helloserver.entity.User;
import com.example.helloserver.mapper.UserMapper;
import com.example.helloserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service // 必须添加该注解，将业务类交给 Spring 容器管理
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public Result<String> register(UserDTO userDTO) {
        // 1. 校验用户是否已存在
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, userDTO.getUsername());
        User exist = userMapper.selectOne(wrapper);
        if (exist != null) {
            return Result.error(ResultCode.USER_HAS_EXISTED);
        }

        // 2. 存入数据库
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(userDTO.getPassword());
        userMapper.insert(user);

        return Result.success("注册成功");
    }

    @Override
    public Result<String> login(UserDTO userDTO) {
        // 1. 校验用户是否存在
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, userDTO.getUsername());
        User dbUser = userMapper.selectOne(wrapper);
        if (dbUser == null) {
            return Result.error(ResultCode.USER_NOT_EXIST);
        }

        // 2. 校验密码是否正确
        if (!dbUser.getPassword().equals(userDTO.getPassword())) {
            return Result.error(ResultCode.PASSWORD_ERROR);
        }

        return Result.success("登录成功");
    }

    @Override
    public Result<String> getUserById(Long id) {
        User user = userMapper.selectById(id);
        return user == null ? Result.error(ResultCode.ERROR) : Result.success(user.toString());
    }

    @Override
    public Result<Object> getUsersPage(Integer pageNum, Integer pageSize) {
        // 1. 创建分页对象（参数1：当前页码，参数2：每页显示条数）
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<User> pageParam = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageNum, pageSize);
        
        // 2. 执行分页查询（参数1：分页对象，参数2：查询条件 Wrapper，这里传 null 代表查询所有）
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<User> resultPage = userMapper.selectPage(pageParam, null);
        
        // 3. 返回结果（resultPage 中包含了 records 数据列表、total 总条数、pages 总页数等信息）
        return Result.success(resultPage);
    }
}