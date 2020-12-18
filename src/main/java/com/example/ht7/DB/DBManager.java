package com.example.ht7.DB;

import com.example.ht7.entities.ShopItems;


import java.util.ArrayList;

public class DBManager {
    public static ArrayList<ShopItem> itemsList = new ArrayList<>();
    public static ArrayList<ShopItems> shopItems = new ArrayList<>();

    static {
        itemsList.add(new ShopItem(1L, "Huawei Mate 30 Pro", "Beginning. Waters blessed good so. Itself Life. Own above be.", 24999, 10, 5, "https://www.mechta.kz/export/1cbitrix/import_files/65/6566a47c-5dd0-11ea-a22d-005056b6dbd7.jpeg"));
        itemsList.add(new ShopItem(2L, "Huawei Mate 30 Pro", "Beginning. Waters blessed good so. Itself Life. Own above be.", 24999, 10, 5, "https://www.mechta.kz/export/1cbitrix/import_files/65/6566a47c-5dd0-11ea-a22d-005056b6dbd7.jpeg"));
        itemsList.add(new ShopItem(3L, "Huawei Mate 30 Pro", "Beginning. Waters blessed good so. Itself Life. Own above be.", 24999, 10, 5, "https://www.mechta.kz/export/1cbitrix/import_files/65/6566a47c-5dd0-11ea-a22d-005056b6dbd7.jpeg"));
        itemsList.add(new ShopItem(4L, "Huawei Mate 30 Pro", "Beginning. Waters blessed good so. Itself Life. Own above be.я", 24999, 10, 5, "https://www.mechta.kz/export/1cbitrix/import_files/65/6566a47c-5dd0-11ea-a22d-005056b6dbd7.jpeg"));
    }

    static Long id = 5L;

    public static ArrayList<ShopItem> getAllItems() {return itemsList;}

    static ShopItem getItem(Long id) {
        for (ShopItem item : itemsList) {
            if (item.getId().equals(id)) {
                return item;
            }
        }
        return null;
    }

    public static void addItem(ShopItem item) {
        item.setId(id);
        itemsList.add(item);
        id++;
    }

    public static void deleteItem(Long id) {
        for (int i = 0; i < itemsList.size(); i++) {
            if (itemsList.get(i).getId().equals(id)) {
                itemsList.remove(i);
                break;
            }
        }
    }

    public static void saveItem(ShopItem item) {
        for (ShopItem items : itemsList) {
            if (items.getId().equals(item.getId())) {
                items.setName(item.getName());
                items.setDescription(item.getDescription());
                items.setPrice(item.getPrice());
                items.setAmount(item.getAmount());
                items.setStars(item.getStars());
                items.setPictureUrl(item.getPictureUrl());
            }
        }
    }
}
