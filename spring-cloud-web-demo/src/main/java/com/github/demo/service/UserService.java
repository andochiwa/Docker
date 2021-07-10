package com.github.demo.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.demo.entity.User;
import com.github.demo.mapper.UserMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author HAN
 * @since 2021-07-11
 */
@Service
public class UserService extends ServiceImpl<UserMapper, User> {

    public List<User> getAll() {
        return list();
    }
}
