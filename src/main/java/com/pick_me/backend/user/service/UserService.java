package com.pick_me.backend.user.service;

import com.pick_me.backend.user.entity.User;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    User one(@NotNull Integer id);

    User save(User user);

    User update(User user);

    void delete(Integer id);
}
