package com.example.ht7.repositories;

import com.example.ht7.entities.Comment;
import com.example.ht7.entities.ShopItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByItems(ShopItems item);
}
