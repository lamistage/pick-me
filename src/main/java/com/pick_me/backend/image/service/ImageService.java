package com.pick_me.backend.image.service;

import com.pick_me.backend.image.entity.Image;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ImageService {
    Page<Image> page(Pageable pageable, @Nullable List<Integer> tagIds, @Nullable List<Integer> userIds, String sort);

    Image one(@NotNull Integer id);

    Image save(Image image);

    Image update(Image image);

    void remove(Integer id);
}
