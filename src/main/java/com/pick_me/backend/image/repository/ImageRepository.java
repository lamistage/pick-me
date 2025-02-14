package com.pick_me.backend.image.repository;

import com.pick_me.backend.image.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface ImageRepository extends JpaRepository<Image, Integer>{
}
