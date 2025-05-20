package com.pick_me.backend.image.service;

import com.pick_me.backend.dto.ImageDTO;
import com.pick_me.backend.image.entity.QImage;
import com.pick_me.backend.tag.repository.TagRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.pick_me.backend.image.entity.Image;
import com.pick_me.backend.image.repository.ImageRepository;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ImageServiceImpl implements ImageService {

    private static final BooleanExpression TRUE_EXPRESSION = Expressions.asBoolean(true).isTrue();

    private final ImageRepository imageRepository;
    private final TagRepository tagRepository;

    public ImageServiceImpl(ImageRepository imageRepository,
                            TagRepository tagRepository) {
        this.imageRepository = imageRepository;
        this.tagRepository = tagRepository;
    }

    @Override
    public Page<ImageDTO> page(Pageable pageable, @Nullable List<String> tags, @Nullable List<String> userLogins, String sort) {
        log.info("Get images: tags={}, userLogins={}, sort={}, page={}, size={}",
                tags, userLogins, sort, pageable.getPageNumber(), pageable.getPageSize());
        BooleanExpression predicate = getPredicate(tags, userLogins);
        Page<Image> sortedImages;

        try {
            if (sort == null || sort.isEmpty()) {
                sortedImages = imageRepository.findAll(
                        predicate,
                        PageRequest.of(
                                pageable.getPageNumber(),
                                pageable.getPageSize(),
                                Sort.by("date").ascending()));
            } else {
                sortedImages = imageRepository.findAll(
                        predicate,
                        PageRequest.of(
                                pageable.getPageNumber(),
                                pageable.getPageSize(),
                                createSort(sort)
                        ));
            }
            log.info("Found {} images", sortedImages.getTotalElements());
            return sortedImages.map(this::convertToImageDTO);
        } catch (Exception e) {
            log.error("Error getting images: tags={}, userLogins={}, sort={}, error{}", tags, userLogins, sort, e.getMessage(), e);
            throw e;
        }

    }

    private BooleanExpression getPredicate(@Nullable List<String> tags, @Nullable List<String> userLogins) {
        QImage qImage = QImage.image;
        BooleanExpression tagIdsPredicate = getTagsPredicate(tags, qImage);
        BooleanExpression userIdsPredicate = getUserLoginsPredicate(userLogins, qImage);
        BooleanExpression predicate;
        predicate = (tagIdsPredicate != null ? tagIdsPredicate : TRUE_EXPRESSION)
                .and(userIdsPredicate != null ? userIdsPredicate : TRUE_EXPRESSION);

        log.debug("Constructed predicate: {}", predicate);
        return predicate;
    }

    private BooleanExpression getTagsPredicate(@Nullable List<String> tags, QImage qImage) {
        return tags != null ?
                qImage.tags.any().name.in(tags)
                : null;
    }

    private BooleanExpression getUserLoginsPredicate(@Nullable List<String> userLogins, QImage qImage) {
        return userLogins != null ?
                qImage.user.login.in(userLogins)
                : null;
    }

    private Sort createSort(String sortLine) {
        log.debug("Creating sort from string: {}", sortLine);
        String[] pairs = sortLine.split(",");
        List<Sort.Order> orders = new ArrayList<>();

        for (int i = 0; i < pairs.length; i += 2) {
            String property = pairs[i];
            String direction = pairs[i + 1].toUpperCase();

            if ("ASC".equals(direction)) {
                orders.add(Sort.Order.asc(property));
            } else if ("DESC".equals(direction)) {
                orders.add(Sort.Order.desc(property));
            }
        }

        Sort sort = Sort.by(orders);
        log.debug("Created sort: {}", sort);
        return sort;
    }

    @Override
    public ImageDTO one(Integer id) {
        log.info("Get image with id={}", id);
        try {
            Optional<Image> imageOptional = imageRepository.findById(id);
            if (imageOptional.isEmpty()) {
                log.warn("Image not found with id={}", id);
                throw new RuntimeException("Image not found with id: " + id);
            }
            return convertToImageDTO(imageOptional.get());
        } catch (Exception e) {
            log.error("Error getting image by id={}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public ImageDTO save(Image image) {
        log.info("Saving new image");
        try {
            tagRepository.saveAll(image.getTags());
            Image savedImage = imageRepository.save(image);
            log.info("Image saved with id={}", savedImage.getId());
            return convertToImageDTO(savedImage);
        } catch (Exception e) {
            log.error("Error saving image: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public ImageDTO update(Image image) {
        log.info("Updating image with id={}", image.getId());
        try {
            if (!imageRepository.existsById(image.getId())) {
                log.warn("Image not found while updating with id={}", image.getId());
                throw new RuntimeException("Image not found with id: " + image.getId());
            }
            Image updatedImage = imageRepository.save(image);
            log.info("Image updated with id={}", updatedImage.getId());
            return convertToImageDTO(updatedImage);
        } catch (Exception e) {
            log.error("Error updating image with id={}: {}", image.getId(), e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void remove(Integer id) {
        log.info("Removing image with id={}", id);
        try {
            if (!imageRepository.existsById(id)) {
                log.warn("Image not found while removing with id={}", id);
                throw new RuntimeException("Image not found with id: " + id);
            }
            this.imageRepository.deleteById(id);
            log.info("Image removed with id={}", id);
        } catch (Exception e) {
            log.error("Error removing image with id={}: {}", id, e.getMessage(), e);
        }
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
}
