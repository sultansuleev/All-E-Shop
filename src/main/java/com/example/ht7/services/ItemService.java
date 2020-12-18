package com.example.ht7.services;

import com.example.ht7.DB.ShopItem;
import com.example.ht7.entities.*;

import java.util.List;

public interface ItemService {
    ShopItems addItem(ShopItems item);
    List<ShopItems> getAllItems();
    ShopItems getItem(Long id);
    void deleteItem(ShopItems item);
    ShopItems saveItem(ShopItems item);
    List<Picture> getAllPictures(ShopItems item);
    Picture addPicture(Picture picture);
    void deletePicture(Picture picture);
    Picture getPicture(Long id);
    List<ShopItems> getItemsInTop(boolean top);
    List<ShopItems> getItemsByNamePriceAsc(String name);
    List<ShopItems> getItemsByNamePriceDesc(String name);
    List<ShopItems> getItemsByNameAndPriceBetweenOrderByPriceDesc(String name,double price1,double price2);
    List<ShopItems> getItemsByNameAndPriceBetweenOrderByPriceAsc(String name,double price1,double price2);
    List<ShopItems> getItemsByBrand(Brands brand);
    List<ShopItems> getItemsByBrandAndByNamePriceAsc(Brands brand, String name);
    List<ShopItems> getItemsByBrandAndByNamePriceDesc(Brands brand, String name);
    List<ShopItems> getItemsByBrandsAndByNameAndPriceBetweenOrderByPriceDesc(Brands brand, String name, double price1, double price2);
    List<ShopItems> getItemsByBrandsAndByNameAndPriceBetweenOrderByPriceAsc(Brands brand, String name, double price1, double price2);
    List<ShopItems> getAllByCategories(Categories categories);


    List<Countries> getAllCountries();
    Countries addCountry(Countries country);
    Countries saveCountry(Countries country);
    Countries getCountry(Long id);
    void deleteCountry(Countries countries);

    List<Brands> getAllBrands();
    Brands getBrand(Long id);
    Brands saveBrands(Brands brands);
    Brands addBrand (Brands brands);
    void deleteBrand(Brands brands);

    List<Categories> getAllCategories();
    Categories getCategory(Long id);
    Categories saveCategory(Categories category);
    Categories addCategory (Categories category);
    void deleteCategory(Categories category);

    List<Comment> getAllCommentsByItem(ShopItems item);
    Comment getComment(Long id);
    Comment saveComment(Comment comment);
    Comment addComment(Comment comment);
    void deleteComment(Comment comment);
}
