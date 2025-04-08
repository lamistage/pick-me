package com.pick_me.backend.favorites.controller;

import com.pick_me.backend.dto.ImageDTO;
import com.pick_me.backend.dto.UserDTO;
import com.pick_me.backend.favorites.service.LikeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class LikeControllerImpl implements LikeController {

    private final LikeService likeService;

    public LikeControllerImpl(LikeService likeService) {
        this.likeService = likeService;
    }

    @Override
    public void addToFavorites(Integer userId, Integer imageId) {
        this.likeService.addToFavorites(userId, imageId);
    }

    @Override
    public void removeFromFavorites(Integer userId, Integer imageId) {
        this.likeService.removeFromFavorites(userId, imageId);
    }

    @Override
    public Page<ImageDTO> getFavorites(Integer userId, Pageable pageable, List<String> tags, List<String> userLogins, String sort) {
        return likeService.getFavorites(userId, pageable, tags, userLogins, sort);
    }

    @Override
    public List<UserDTO> getUsersWhoLiked(Integer imageId) {
        return likeService.getUsersWhoLiked(imageId);
    }

    @Override
    public Boolean isLikedByUser(Integer userId, Integer imageId) {
        return likeService.isLikedByUser(userId, imageId);
    }
}
