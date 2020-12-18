package com.example.ht7.repositories;

import com.example.ht7.entities.Brands;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface BrandRepository extends JpaRepository<Brands, Long> {

}
