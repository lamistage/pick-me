package com.pick_me.backend.user.repository;

import com.pick_me.backend.user.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
    User findByLogin(String login);

    User findByEmail(String email);

    List<User> findAll();
}
