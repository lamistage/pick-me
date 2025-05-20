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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
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
        log.info("Add to favorites: userId={}, imageId={}", userId, imageId);
        try {
            User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
            Image image = imageRepository.findById(imageId).orElseThrow(() -> new RuntimeException("Image not found with id: " + imageId));

            if (!user.getFavorites().contains(image)) {
                user.getFavorites().add(image);
                userRepository.save(user);
                log.info("Image id={} added to favorites for user id={}", imageId, userId);
            } else {
                log.info("Image id={} already in favorites for user id={}", imageId, userId);
            }
        } catch (RuntimeException e) {
            log.error("Failed to ad to favorites: userId={}, imageId={}, error={}", userId, imageId, e.getMessage(), e);
            throw e;
        }


    }

    @Override
    @Transactional
    public void removeFromFavorites(Integer userId, Integer imageId) {
        log.info("Remove from favorites: userId={}, imageId={}", userId, imageId);
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
            Image image = imageRepository.findById(imageId)
                    .orElseThrow(() -> new RuntimeException("Image not found with id: " + imageId));

            if (user.getFavorites().contains(image)) {
                user.getFavorites().remove(image);
                userRepository.save(user);
                log.info("Image id={} removed from favorites for user id={}", imageId, userId);
            } else {
                log.info("Image id={} was not in favorites for user id={}", imageId, userId);
            }
        } catch (RuntimeException e) {
            log.error("Failed to remove from favorites: userId={}, imageId={}, error={}", userId, imageId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Page<ImageDTO> getFavorites(Integer userId, Pageable pageable, List<String> tags, List<String> userLogins, String sort) {
        log.info("Get favorites: userId={}, tags={}, userLogins={}, sort={}, page={}, size={}",
                userId, tags, userLogins, sort, pageable.getPageNumber(), pageable.getPageSize());
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

            List<Image> favorites = user.getFavorites();
            if (favorites.isEmpty()) {
                log.info("No favorites found for user id={}", userId);
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
            log.info("Found {} favorites for user id={}", filteredFavorites.getTotalElements(), userId);
            return filteredFavorites.map(this::convertToImageDTO);
        } catch (RuntimeException e) {
            log.error("Failed to get favorites for user id={}: {}", userId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<UserDTO> getUsersWhoLiked(Integer imageId) {
        log.info("Get users who liked imageId={}", imageId);
        try {
            Image image = imageRepository.findById(imageId)
                    .orElseThrow(() -> new RuntimeException("Image not found with id: " + imageId));

            List<UserDTO> users = image.getLikedBy().stream()
                    .map(this::convertToUserDTO)
                    .collect(Collectors.toList());
            log.info("Found {} users who liked imageId={}", users.size(), imageId);
            return users;
        } catch (RuntimeException e) {
            log.error("Failed to get users who liked imageId={}: {}", imageId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public boolean isLikedByUser(Integer userId, Integer imageId) {
        log.debug("Check if userId={} liked imageId={}", userId, imageId);
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
            Image image = imageRepository.findById(imageId)
                    .orElseThrow(() -> new RuntimeException("Image not found with id: " + imageId));

            boolean liked = user.getFavorites().contains(image);
            log.debug("User id={} liked image id={}: {}", userId, imageId, liked);
            return liked;
        } catch (RuntimeException e) {
            log.error("Failed to check like status: userId={}, imageId={}, error={}", userId, imageId, e.getMessage(), e);
            throw e;
        }
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
