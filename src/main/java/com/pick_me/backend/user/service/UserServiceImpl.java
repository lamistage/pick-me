package com.pick_me.backend.user.service;

import com.pick_me.backend.dto.UserDTO;
import com.pick_me.backend.user.entity.User;
import com.pick_me.backend.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public User one(Integer id) {
        log.info("Get user with id={}", id);
        return repository.findById(id).get();
    }

    @Override
    public User save(User user) {
        log.info("Save user with id={}", user.getId());
        return repository.save(user);
    }

    @Override
    public User update(User user) {
        log.info("Update user with id={}", user.getId());
        return repository.save(user);
    }

    @Override
    public void delete(Integer id) {
        log.info("Delete user with id={}", id);
        this.repository.deleteById(id);
    }

    @Override
    public List<UserDTO> get() {
        log.info("Get all users");
        List<User> users = repository.findAll();
        return users.stream().map(UserDTO::new).collect(Collectors.toList());
    }
}
