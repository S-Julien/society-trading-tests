package io.github.chakyl.societytrading.util;


import it.unimi.dsi.fastutil.Hash;
import net.minecraft.world.item.Item;

public final class ItemHash implements Hash.Strategy<Item>
{
    public static final ItemHash INSTANCE = new ItemHash();

    private ItemHash() {}

    @Override
    public int hashCode(Item o)
    {
        return Item.getId(o);
    }

    @Override
    public boolean equals(Item a, Item b)
    {
        return a == b;
    }
}