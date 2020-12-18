package com.example.ht7.services.impl;

import com.example.ht7.entities.*;
import com.example.ht7.repositories.*;
import com.example.ht7.services.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {
    @Autowired
    private ShopItemRepository shopItemRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PictureRepository pictureRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Override
    public ShopItems addItem(ShopItems item) {
        return shopItemRepository.save(item);
    }

    @Override
    public List<ShopItems> getItemsInTop(boolean top){
        return shopItemRepository.findAllByInTopPageEquals(top);
    }

    @Override
    public List<ShopItems> getAllItems() {
        return shopItemRepository.findAll();
    }

    @Override
    public ShopItems getItem(Long id) {
        return shopItemRepository.getOne(id);
    }

    @Override
    public void deleteItem(ShopItems item) {
        shopItemRepository.delete(item);
    }

    @Override
    public ShopItems saveItem(ShopItems item) {
        return shopItemRepository.save(item);
    }

    @Override
    public List<ShopItems> getItemsByBrand(Brands brand) {
        return shopItemRepository.findAllByBrands(brand);
    }

    @Override
    public List<ShopItems> getItemsByNamePriceAsc(String name) {
        return shopItemRepository.findAllByNameContainingOrderByPriceAsc(name);
    }

    @Override
    public List<ShopItems> getItemsByNamePriceDesc(String name) {
        return shopItemRepository.findAllByNameContainingOrderByPriceDesc(name);
    }

    @Override
    public List<ShopItems> getItemsByNameAndPriceBetweenOrderByPriceDesc(String name, double price1, double price2) {
        return shopItemRepository.findAllByNameContainingAndPriceIsBetweenOrderByPriceDesc(name,price1,price2);
    }

    @Override
    public List<ShopItems> getItemsByNameAndPriceBetweenOrderByPriceAsc(String name, double price1, double price2) {
        return shopItemRepository.findAllByNameContainingAndPriceIsBetweenOrderByPriceAsc(name,price1,price2);
    }

    @Override
    public List<ShopItems> getItemsByBrandAndByNamePriceAsc(Brands brand, String name) {
        return shopItemRepository.findAllByBrandsAndNameContainingOrderByPriceAsc(brand,name);
    }

    @Override
    public List<ShopItems> getItemsByBrandAndByNamePriceDesc(Brands brand, String name) {
        return shopItemRepository.findAllByBrandsAndNameContainingOrderByPriceDesc(brand,name);
    }

    @Override
    public List<ShopItems> getItemsByBrandsAndByNameAndPriceBetweenOrderByPriceDesc(Brands brand, String name, double price1, double price2) {
        return shopItemRepository.findAllByBrandsAndNameContainingAndPriceIsBetweenOrderByPriceDesc(brand,name,price1,price2);
    }

    @Override
    public List<ShopItems> getItemsByBrandsAndByNameAndPriceBetweenOrderByPriceAsc(Brands brand, String name, double price1, double price2) {
        return shopItemRepository.findAllByBrandsAndNameContainingAndPriceIsBetweenOrderByPriceAsc(brand,name,price1,price2);
    }

    @Override
    public List<Countries> getAllCountries() {
        return countryRepository.findAll();
    }

    @Override
    public Countries addCountry(Countries country) {
        return countryRepository.save(country);
    }

    @Override
    public Countries saveCountry(Countries country) {
        return countryRepository.save(country);
    }

    @Override
    public Countries getCountry(Long id) {
        return countryRepository.getOne(id);
    }

    @Override
    public List<Brands> getAllBrands() {
        return brandRepository.findAll();
    }

    @Override
    public Brands getBrand(Long id) {
        return brandRepository.getOne(id);
    }

    @Override
    public Brands saveBrands(Brands brands) {
        return brandRepository.save(brands);
    }

    @Override
    public Brands addBrand(Brands brands) {
        return brandRepository.save(brands);
    }


    @Override
    public Categories addCategory(Categories category) {
        return categoryRepository.save(category);
    }

    @Override
    public List<Categories> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Categories getCategory(Long id) {
        return categoryRepository.getOne(id);
    }

    @Override
    public Categories saveCategory(Categories category) {
        return categoryRepository.save(category);
    }

    @Override
    public void deleteCountry(Countries country) {
        countryRepository.delete(country);
    }

    @Override
    public void deleteBrand(Brands brand) {
        brandRepository.delete(brand);
    }

    @Override
    public void deleteCategory(Categories category) {
        categoryRepository.delete(category);
    }

    @Override
    public List<Comment> getAllCommentsByItem(ShopItems item) {
        return commentRepository.findAllByItems(item);
    }

    @Override
    public Comment getComment(Long id) {
        return commentRepository.getOne(id);
    }

    @Override
    public Comment saveComment(Comment comment) {
        return commentRepository.save(comment);
    }

    @Override
    public Comment addComment(Comment comment) {
        return commentRepository.save(comment);
    }

    @Override
    public void deleteComment(Comment comment) {
        commentRepository.delete(comment);
    }

    @Override
    public List<ShopItems> getAllByCategories(Categories category) {
        return shopItemRepository.findAllByCategories(category);
    }

    @Override
    public List<Picture> getAllPictures(ShopItems item) {
        return pictureRepository.findAllByItem(item);
    }

    @Override
    public Picture addPicture(Picture picture) {
        return pictureRepository.save(picture);
    }

    @Override
    public void deletePicture(Picture picture) {
        pictureRepository.delete(picture);
    }

    @Override
    public Picture getPicture(Long id) {
        return pictureRepository.getOne(id);
    }


}
