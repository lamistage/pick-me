package com.pick_me.backend.image.service;

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
    public Page<Image> page(Pageable pageable, @Nullable List<Integer> tagIds, @Nullable List<Integer> userIds, String sort) {
        BooleanExpression predicate = getPredicate(tagIds, userIds);
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

        return sortedImages;
    }

    private BooleanExpression getPredicate(@Nullable List<Integer> tagIds, @Nullable List<Integer> userIds) {
        QImage qImage = QImage.image;
        BooleanExpression tagIdsPredicate = getTagIdsPredicate(tagIds, qImage);
        BooleanExpression userIdsPredicate = getUserIdsPredicate(userIds, qImage);
        BooleanExpression predicate;
        predicate = (tagIdsPredicate != null ? tagIdsPredicate : TRUE_EXPRESSION)
                .and(userIdsPredicate != null ? userIdsPredicate : TRUE_EXPRESSION);

        return predicate;
    }

    private BooleanExpression getTagIdsPredicate(@Nullable List<Integer> tagIds, QImage qImage) {
        return tagIds != null ?
                qImage.tags.any().id.in(tagIds)
                : null;
    }

    private BooleanExpression getUserIdsPredicate(@Nullable List<Integer> userIds, QImage qImage) {
        return userIds != null ?
                qImage.user.id.in(userIds)
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
    public Image one(Integer id) {
        return imageRepository.findById(id).get();
    }

    @Override
    public Image save(Image image) {
        tagRepository.saveAll(image.getTags());
        return imageRepository.save(image);
    }

    @Override
    public Image update(Image image) {
        return imageRepository.save(image);
    }

    @Override
    public void remove(Integer id) {
        this.imageRepository.deleteById(id);
    }
}
