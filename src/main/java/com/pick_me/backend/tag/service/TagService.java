package com.pick_me.backend.tag.service;

import com.pick_me.backend.tag.entity.Tag;
import org.springframework.stereotype.Service;

@Service
public interface TagService {
    Tag save(Tag tag);
}
