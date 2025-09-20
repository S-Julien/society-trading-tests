package io.github.chakyl.societytrading.JEI;

import io.github.chakyl.societytrading.data.Shop;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;

public class ShopRecipe {
    final String shopId;
    final MutableComponent shopName;
    final ItemStack catalyst;
    final String texture;

    public ShopRecipe(Shop shop) {
        this.shopId = shop.shopID();
        this.shopName = shop.name();
        this.catalyst = shop.jeiCatalyst();
        this.texture = shop.texture();
    }
}
