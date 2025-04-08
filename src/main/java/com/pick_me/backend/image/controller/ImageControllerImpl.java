package com.pick_me.backend.image.controller;

import com.pick_me.backend.dto.ImageDTO;
import com.pick_me.backend.image.entity.Image;
import com.pick_me.backend.image.service.ImageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class ImageControllerImpl implements ImageController{

    private final ImageService service;

    public ImageControllerImpl(ImageService service) {
        this.service = service;
    }

    @Override
    public Page<ImageDTO> page(Pageable pageable, List<String> tags, List<String> userLogins, String sort) {
        return service.page(pageable, tags, userLogins, sort);
    }

    @Override
    public ImageDTO one(Integer imageId) {
        return service.one(imageId);
    }

    @Override
    public ImageDTO save(Image image) {
        return service.save(image);
    }

    @Override
    public ImageDTO update(Image image) {
        return service.update(image);
    }

    @Override
    public void remove(Integer imageId) {
        this.service.remove(imageId);
    }
}
