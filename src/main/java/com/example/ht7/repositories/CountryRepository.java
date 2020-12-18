package com.example.ht7.repositories;

import com.example.ht7.entities.Countries;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface CountryRepository extends JpaRepository<Countries, Long> {

}
