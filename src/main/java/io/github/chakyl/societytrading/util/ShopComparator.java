package io.github.chakyl.societytrading.util;

import io.github.chakyl.societytrading.data.Shop;

import java.util.Comparator;

public class ShopComparator implements Comparator<Shop> {
    public enum Order {ID}

    private Order sortingBy = Order.ID;

    @Override
    public int compare(Shop shop1, Shop shop2) {
        switch(sortingBy) {
            case ID: return shop1.shopID().compareTo(shop2.shopID());
        }
        throw new RuntimeException("Can't sort");
    }

    public void setSortingBy(Order sortBy) {
        this.sortingBy = sortingBy;
    }
}