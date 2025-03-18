package com.pick_me.backend.tag.controller;

import com.pick_me.backend.tag.entity.Tag;
import com.pick_me.backend.tag.repository.TagRepository;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class TagControllerImpl implements TagController {
    public final TagRepository repository;

    public TagControllerImpl(TagRepository repository) {
        this.repository = repository;
    }

    @Override
    public Tag save(Tag tag) {
        return repository.save(tag);
    }

    @Override
    public List<Tag> getAllTags() {
        return repository.findAll();
    }
}
