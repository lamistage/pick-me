package com.pick_me.backend.favorites.service;

import com.pick_me.backend.dto.ImageDTO;
import com.pick_me.backend.dto.UserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface LikeService {
    void addToFavorites(Integer userId, Integer imageId);

    void removeFromFavorites(Integer userId, Integer imageId);

    Page<ImageDTO> getFavorites(Integer userId, Pageable pageable, List<String> tags, List<String> userLogins, String sort);

    List<UserDTO> getUsersWhoLiked(Integer imageId);

    boolean isLikedByUser(Integer userId, Integer imageId);
}
