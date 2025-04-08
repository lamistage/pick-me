package com.pick_me.backend.image.service;

import com.pick_me.backend.dto.ImageDTO;
import com.pick_me.backend.image.entity.Image;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ImageService {
    Page<ImageDTO> page(Pageable pageable, @Nullable List<String> tags, @Nullable List<String> userLogins, String sort);

    ImageDTO one(@NotNull Integer id);

    ImageDTO save(Image image);

    ImageDTO update(Image image);

    void remove(Integer id);
}
