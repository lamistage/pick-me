package com.pick_me.backend.image.controller;

import com.pick_me.backend.image.entity.Image;
import com.pick_me.backend.image.service.ImageService;
import org.springframework.data.crossstore.ChangeSetPersister;
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
    public Page<Image> list(Pageable pageable, List<String> sort, List<Integer> tags, List<Integer> users) {
        return null;
    }

    @Override
    public Image one(Integer imageId) {
        return service.one(imageId);
    }

    @Override
    public Image save(Image image) {
        return service.save(image);
    }

    @Override
    public Image update(Image image) {
        return service.update(image);
    }

    @Override
    public void remove(Integer imageId) {
        this.service.remove(imageId);
    }
}
