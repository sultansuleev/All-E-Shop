package com.example.ht7.repositories;

import com.example.ht7.entities.Picture;
import com.example.ht7.entities.ShopItems;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PictureRepository extends JpaRepository<Picture, Long> {
    List<Picture> findAllByItem(ShopItems item);
}
