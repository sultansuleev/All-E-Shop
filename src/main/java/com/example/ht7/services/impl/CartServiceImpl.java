package com.example.ht7.services.impl;

import com.example.ht7.entities.Cart;
import com.example.ht7.repositories.CartRepository;
import com.example.ht7.services.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private CartRepository cartRepository;

    @Override
    public Cart addCart(Cart cart) {
        return null;
    }

    @Override
    public List<Cart> getCarts() {
        return null;
    }

    @Override
    public Cart getCart(Long id) {
        return null;
    }

    @Override
    public void deleteCart(Cart cart) {

    }

    @Override
    public Cart saveCart(Cart cart) {
        return null;
    }
}
