package com.pick_me.backend.user.controller;

import com.pick_me.backend.dto.UserDTO;
import com.pick_me.backend.user.entity.User;
import org.springframework.stereotype.Controller;
import com.pick_me.backend.user.service.UserService;

import java.util.List;

@Controller
public class UserControllerImpl implements UserController {
    private final UserService service;

    public UserControllerImpl(UserService service) {
        this.service = service;
    }

    @Override
    public User one(Integer userId) {
        return service.one(userId);
    }

    @Override
    public User save(User user) {
        return service.save(user);
    }

    @Override
    public User update(User user) {
        return service.update(user);
    }

    @Override
    public void remove(Integer userId) {
        this.service.delete(userId);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return this.service.get();
    }
}
