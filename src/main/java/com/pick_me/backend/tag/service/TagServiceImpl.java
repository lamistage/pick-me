package com.pick_me.backend.tag.service;

import com.pick_me.backend.tag.entity.Tag;
import com.pick_me.backend.tag.repository.TagRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TagServiceImpl implements TagService {
    public final TagRepository repository;

    public TagServiceImpl(TagRepository repository) {
        this.repository = repository;
    }

    @Override
    public Tag save(Tag tag) {
        log.info("Save tag '{}'", tag.getName());
        return repository.save(tag);
    }
}
