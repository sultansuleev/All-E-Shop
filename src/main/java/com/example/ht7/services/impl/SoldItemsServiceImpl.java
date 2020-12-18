package com.example.ht7.services.impl;

import com.example.ht7.entities.SoldItems;
import com.example.ht7.repositories.SoldItemRepository;
import com.example.ht7.services.SoldItemsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SoldItemsServiceImpl implements SoldItemsService {
    @Autowired
    private SoldItemRepository soldItemRepository;


    @Override
    public SoldItems addItem(SoldItems items) {
        return soldItemRepository.save(items);
    }

    @Override
    public List<SoldItems> getItems() {
        return soldItemRepository.findAll();
    }

    @Override
    public SoldItems getItem(Long id) {
        return soldItemRepository.getOne(id);
    }

    @Override
    public void deleteItem(SoldItems item) {
        soldItemRepository.delete(item);
    }

    @Override
    public List<SoldItems> getItemsByCart(Long cart_id) {
        return soldItemRepository.findAllByCart(cart_id);
    }
}
