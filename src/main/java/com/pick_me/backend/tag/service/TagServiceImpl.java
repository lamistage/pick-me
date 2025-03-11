package com.pick_me.backend.tag.service;

import com.pick_me.backend.tag.entity.Tag;
import com.pick_me.backend.tag.repository.TagRepository;
import org.springframework.stereotype.Service;

@Service
public class TagServiceImpl implements TagService {
    public final TagRepository repository;

    public TagServiceImpl(TagRepository repository) {
        this.repository = repository;
    }

    @Override
    public Tag save(Tag tag) {
        return repository.save(tag);
    }
}
