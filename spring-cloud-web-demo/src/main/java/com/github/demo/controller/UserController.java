package com.github.demo.controller;


import com.github.demo.entity.User;
import com.github.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author HAN
 * @since 2021-07-11
 */
@RestController
@RequestMapping("/demo")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public List<User> getAllData() {
        return userService.getAll();
    }
}

