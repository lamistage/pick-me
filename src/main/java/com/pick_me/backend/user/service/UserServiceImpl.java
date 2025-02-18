package com.pick_me.backend.user.service;

import com.pick_me.backend.user.entity.User;
import com.pick_me.backend.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public User one(Integer id) {
        return repository.findById(id).get();
    }

    @Override
    public User save(User user) {
        return repository.save(user);
    }

    @Override
    public User update(User user) {
        return repository.save(user);
    }

    @Override
    public void delete(Integer id) {
        this.repository.deleteById(id);
    }
}
