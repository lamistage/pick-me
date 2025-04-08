package com.pick_me.backend.favorites.service;

import com.pick_me.backend.dto.ImageDTO;
import com.pick_me.backend.dto.UserDTO;
import com.pick_me.backend.image.entity.Image;
import com.pick_me.backend.image.entity.QImage;
import com.pick_me.backend.image.repository.ImageRepository;
import com.pick_me.backend.user.entity.User;
import com.pick_me.backend.user.repository.UserRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LikeServiceImpl implements LikeService {
    private final UserRepository userRepository;
    private final ImageRepository imageRepository;

    @Autowired
    public LikeServiceImpl(UserRepository userRepository, ImageRepository imageRepository) {
        this.userRepository = userRepository;
        this.imageRepository = imageRepository;
    }

    @Override
    @Transactional
    public void addToFavorites(Integer userId, Integer imageId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        Image image = imageRepository.findById(imageId).orElseThrow(() -> new RuntimeException("Image not found with id: " + imageId));

        if (!user.getFavorites().contains(image)) {
            user.getFavorites().add(image);
            userRepository.save(user);
        }
    }

    @Override
    @Transactional
    public void removeFromFavorites(Integer userId, Integer imageId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        Image image = imageRepository.findById(imageId).orElseThrow(() -> new RuntimeException("Image not found with id: " + imageId));

        user.getFavorites().remove(image);
        userRepository.save(user);
    }

    @Override
    public Page<ImageDTO> getFavorites(Integer userId, Pageable pageable, List<String> tags, List<String> userLogins, String sort) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        List<Image> favorites = user.getFavorites();
        if (favorites.isEmpty()) {
            return new PageImpl<>(List.of(), pageable, 0);
        }

        QImage qImage = QImage.image;
        BooleanExpression predicate = qImage.in(favorites);

        if (tags != null && !tags.isEmpty()) {
            predicate = predicate.and(qImage.tags.any().name.in(tags));
        }

        if (userLogins != null && !userLogins.isEmpty()) {
            predicate = predicate.and(qImage.user.login.in(userLogins));
        }

        Pageable sortedPageable = applySorting(pageable, sort);

        Page<Image> filteredFavorites = imageRepository.findAll(predicate, sortedPageable);
        return filteredFavorites.map(this::convertToImageDTO);
    }

    @Override
    public List<UserDTO> getUsersWhoLiked(Integer imageId) {
        Image image = imageRepository.findById(imageId).orElseThrow(() -> new RuntimeException("Image not found with id: " + imageId));

        return image.getLikedBy().stream().map(this::convertToUserDTO).collect(Collectors.toList());
    }

    @Override
    public boolean isLikedByUser(Integer userId, Integer imageId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        Image image = imageRepository.findById(imageId).orElseThrow(() -> new RuntimeException("Image not found with id: " + imageId));

        return user.getFavorites().contains(image);
    }

    private Pageable applySorting(Pageable pageable, String sort) {
        Pageable sortedPageable = pageable;
        if (sort != null && !sort.isEmpty()) {
            String[] sortParams = sort.split(",");
            if (sortParams.length == 2) {
                String field = sortParams[0];
                String direction = sortParams[1];
                Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
                Sort.Order order = new Sort.Order(sortDirection, field);
                sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(order));
            }
        }
        return sortedPageable;
    }

    private ImageDTO convertToImageDTO(Image image) {
        ImageDTO imageDTO = new ImageDTO();
        imageDTO.setId(image.getId());
        imageDTO.setFilePath(image.getFilePath());
        imageDTO.setDate(image.getDate());
        imageDTO.setUser(image.getUser());
        imageDTO.setTags(image.getTags());
        return imageDTO;
    }

    private UserDTO convertToUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setLogin(user.getLogin());
        return userDTO;
    }
}
