package io.github.chakyl.societytrading.trading;

import dev.shadowsoffire.placebo.reload.DynamicHolder;
import io.github.chakyl.societytrading.data.Shop;
import io.github.chakyl.societytrading.data.ShopRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class ShopOffers extends ArrayList<ShopOffer> {
    public ShopOffers() {
    }

    private ShopOffers(int size) {
        super(size);
    }

//    public ShopOffers(String shopType) {
//        DynamicHolder<Shop> shop = ShopRegistry.INSTANCE.holder(new ResourceLocation(shopType));
//        shop.get().trades()
//        this.add(new ShopOffer(slime, Items.EMERALD.getDefaultInstance(), 1, 1, 1));
//    }

    @Nullable
    public ShopOffer getRecipeFor(ItemStack pStackA, ItemStack pStackB, int pIndex) {
        if (pIndex > 0 && pIndex < this.size()) {
            ShopOffer ShopOffer1 = this.get(pIndex);
            return ShopOffer1.satisfiedBy(pStackA, pStackB) ? ShopOffer1 : null;
        } else {
            for(int i = 0; i < this.size(); ++i) {
                ShopOffer ShopOffer = this.get(i);
                if (ShopOffer.satisfiedBy(pStackA, pStackB)) {
                    return ShopOffer;
                }
            }

            return null;
        }
    }
//
//    public void writeToStream(FriendlyByteBuf pBuffer) {
//        pBuffer.writeCollection(this, (p_220325_, p_220326_) -> {
//            p_220325_.writeItem(p_220326_.getBaseCostA());
//            p_220325_.writeItem(p_220326_.getResult());
//            p_220325_.writeItem(p_220326_.getCostB());
//            p_220325_.writeBoolean(p_220326_.isOutOfStock());
//            p_220325_.writeInt(p_220326_.getUses());
//            p_220325_.writeInt(p_220326_.getMaxUses());
//            p_220325_.writeInt(p_220326_.getXp());
//            p_220325_.writeInt(p_220326_.getSpecialPriceDiff());
//            p_220325_.writeFloat(p_220326_.getPriceMultiplier());
//            p_220325_.writeInt(p_220326_.getDemand());
//        });
//    }
//
//    public static net.minecraft.world.item.trading.ShopOffers createFromStream(FriendlyByteBuf pBuffer) {
//        return pBuffer.readCollection(net.minecraft.world.item.trading.ShopOffers::new, (p_220328_) -> {
//            ItemStack itemstack = p_220328_.readItem();
//            ItemStack itemstack1 = p_220328_.readItem();
//            ItemStack itemstack2 = p_220328_.readItem();
//            boolean flag = p_220328_.readBoolean();
//            int i = p_220328_.readInt();
//            int j = p_220328_.readInt();
//            int k = p_220328_.readInt();
//            int l = p_220328_.readInt();
//            float f = p_220328_.readFloat();
//            int i1 = p_220328_.readInt();
//            ShopOffer ShopOffer = new ShopOffer(itemstack, itemstack2, itemstack1, i, j, k, f, i1);
//            if (flag) {
//                ShopOffer.setToOutOfStock();
//            }
//
//            ShopOffer.setSpecialPriceDiff(l);
//            return ShopOffer;
//        });
//    }
//
//    public CompoundTag createTag() {
//        CompoundTag compoundtag = new CompoundTag();
//        ListTag listtag = new ListTag();
//
//        for(int i = 0; i < this.size(); ++i) {
//            ShopOffer ShopOffer = this.get(i);
//            listtag.add(ShopOffer.createTag());
//        }
//
//        compoundtag.put("Recipes", listtag);
//        return compoundtag;
//    }
}