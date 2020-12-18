package com.example.ht7.repositories;

import com.example.ht7.entities.Brands;
import com.example.ht7.entities.Categories;
import com.example.ht7.entities.Comment;
import com.example.ht7.entities.ShopItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface ShopItemRepository extends JpaRepository<ShopItems,Long> {
    List<ShopItems> findAllByPriceGreaterThan(double price);
    List<ShopItems> findAllByInTopPageEquals(boolean isTop);
    ShopItems findByIdAndPriceGreaterThan(Long id, double price);
    List<ShopItems> findAllByNameContainingOrderByPriceAsc(String name);
    List<ShopItems> findAllByNameContainingOrderByPriceDesc(String name);
    List<ShopItems>findAllByNameContainingAndPriceIsBetweenOrderByPriceAsc(String name,double price1,double price2);
    List<ShopItems>findAllByNameContainingAndPriceIsBetweenOrderByPriceDesc(String name,double price1,double price2);
    List<ShopItems> findAllByBrands(Brands brands);
    List<ShopItems> findAllByBrandsAndNameContainingOrderByPriceAsc(Brands brands, String name);
    List<ShopItems> findAllByBrandsAndNameContainingOrderByPriceDesc(Brands brands, String name);
    List<ShopItems> findAllByBrandsAndNameContainingAndPriceIsBetweenOrderByPriceAsc(Brands brands, String name, double price1, double price2);
    List<ShopItems> findAllByBrandsAndNameContainingAndPriceIsBetweenOrderByPriceDesc(Brands brands, String name, double price1, double price2);
    List<ShopItems> findAllByBrandsAndPriceIsBetweenOrderByPriceDesc(Brands brands, double price1, double price2);
    List<ShopItems> findAllByCategories(Categories category);
}
