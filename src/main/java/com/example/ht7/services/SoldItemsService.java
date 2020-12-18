package com.example.ht7.services;

import com.example.ht7.entities.SoldItems;

import java.util.List;

public interface SoldItemsService {
    SoldItems addItem(SoldItems items);

    List<SoldItems> getItems();

    SoldItems getItem(Long id);

    void deleteItem(SoldItems item);

    List<SoldItems> getItemsByCart(Long cart_id);
}
