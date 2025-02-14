package com.pick_me.backend.image.service;

import com.pick_me.backend.image.entity.Image;
import com.pick_me.backend.image.repository.ImageRepository;
import org.springframework.stereotype.Service;

@Service
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;

    public ImageServiceImpl(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    @Override
    public Image one(Integer id) {
        return imageRepository.findById(id).get();
    }

    @Override
    public Image save(Image image) {
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
