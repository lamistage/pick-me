package com.pick_me.backend.image.service;

import com.pick_me.backend.dto.ImageDTO;
import com.pick_me.backend.image.entity.QImage;
import com.pick_me.backend.tag.repository.TagRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.pick_me.backend.image.entity.Image;
import com.pick_me.backend.image.repository.ImageRepository;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        BooleanExpression predicate = getPredicate(tags, userLogins);
        Page<Image> sortedImages;

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

        return sortedImages.map(this::convertToImageDTO);
    }

    private BooleanExpression getPredicate(@Nullable List<String> tags, @Nullable List<String> userLogins) {
        QImage qImage = QImage.image;
        BooleanExpression tagIdsPredicate = getTagsPredicate(tags, qImage);
        BooleanExpression userIdsPredicate = getUserLoginsPredicate(userLogins, qImage);
        BooleanExpression predicate;
        predicate = (tagIdsPredicate != null ? tagIdsPredicate : TRUE_EXPRESSION)
                .and(userIdsPredicate != null ? userIdsPredicate : TRUE_EXPRESSION);

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

        return Sort.by(orders);
    }

    @Override
    public ImageDTO one(Integer id) {
        Optional<Image> imageOptional = imageRepository.findById(id);
        if (imageOptional.isEmpty()) {
            throw new RuntimeException("Image not found with id: " + id);
        }
        return convertToImageDTO(imageOptional.get());
    }

    @Override
    public ImageDTO save(Image image) {
        tagRepository.saveAll(image.getTags());
        Image savedImage = imageRepository.save(image);
        return convertToImageDTO(savedImage);
    }

    @Override
    public ImageDTO update(Image image) {
        if (!imageRepository.existsById(image.getId())) {
            throw new RuntimeException("Image not found with id: " + image.getId());
        }
        Image updatedImage = imageRepository.save(image);
        return convertToImageDTO(updatedImage);
    }

    @Override
    public void remove(Integer id) {
        if (!imageRepository.existsById(id)) {
            throw new RuntimeException("Image not found with id: " + id);
        }
        this.imageRepository.deleteById(id);
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
