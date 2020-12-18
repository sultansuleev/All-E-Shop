package com.example.ht7.repositories;

import com.example.ht7.entities.SoldItems;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SoldItemRepository extends JpaRepository<SoldItems, Long> {
    List<SoldItems> findAllByCart(Long cart_id);
}
