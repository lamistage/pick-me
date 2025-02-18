package com.pick_me.backend.image.repository;

import com.pick_me.backend.image.entity.Image;
import com.pick_me.backend.image.entity.QImage;
import com.querydsl.core.types.dsl.EntityPathBase;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, Integer>, QuerydslPredicateExecutor<Image>, QuerydslBinderCustomizer<EntityPathBase<QImage>> {
    @Override
    default void customize(QuerydslBindings bindings, EntityPathBase<QImage> root) {
    }

    @Override
    @NotNull
    List<Image> findAll();
}
