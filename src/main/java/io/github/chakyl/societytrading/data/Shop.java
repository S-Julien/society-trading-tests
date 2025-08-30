package io.github.chakyl.societytrading.data;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import dev.shadowsoffire.placebo.codec.CodecProvider;
import dev.shadowsoffire.placebo.json.ItemAdapter;
import io.github.chakyl.societytrading.SocietyTrading;
import io.github.chakyl.societytrading.trading.ShopOffer;
import io.github.chakyl.societytrading.trading.ShopOffers;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Stores all of the information representing a Shop.
 *
 * @param shopID The breed of the slime
 * @param name   The display name of slime
 * @param trades Trade object
 */
public record Shop(String shopID, MutableComponent name, ShopOffers trades) implements CodecProvider<Shop> {

    public static final Codec<Shop> CODEC = new ShopCodec();

    public Shop(Shop other) {
        this(other.shopID, other.name, other.trades);
    }

    public int getColor() {
        return this.name.getStyle().getColor().getValue();
    }

    public Shop validate(ResourceLocation key) {
        Preconditions.checkNotNull(this.shopID, "Invalid shop ID!");
        Preconditions.checkNotNull(this.name, "Invalid shop name!");
        return this;
    }

    @Override
    public Codec<? extends Shop> getCodec() {
        return CODEC;
    }

    public static class ShopCodec implements Codec<Shop> {

        @Override
        public <T> DataResult<T> encode(Shop input, DynamicOps<T> ops, T prefix) {
            JsonObject obj = new JsonObject();
            ResourceLocation key = new ResourceLocation(SocietyTrading.MODID, input.shopID);
            obj.addProperty("shopID", input.shopID);
            obj.addProperty("name", ((TranslatableContents) input.name.getContents()).getKey());
            JsonArray trades = new JsonArray();
            obj.add("trades", trades);
            for (ShopOffer trade : input.trades) {
                JsonElement request = ItemAdapter.ITEM_READER.toJsonTree(trade.getCostA());
                JsonObject requestJson = request.getAsJsonObject();
                ResourceLocation requestItemName = new ResourceLocation(requestJson.get("item").getAsString());
                JsonElement secondRequest = ItemAdapter.ITEM_READER.toJsonTree(trade.getCostA());
                JsonObject secondRequestJson = secondRequest.getAsJsonObject();
                ResourceLocation secondRequestItemName = new ResourceLocation(secondRequestJson.get("item").getAsString());
                JsonElement offer = ItemAdapter.ITEM_READER.toJsonTree(trade.getResult());
                JsonObject offerJson = offer.getAsJsonObject();
                ResourceLocation offerItemName = new ResourceLocation(offerJson.get("item").getAsString());
                if (!"minecraft".equals(requestItemName.getNamespace()) && !key.getNamespace().equals(requestItemName.getNamespace())) {
                    requestJson.addProperty("optional", true);
                }
                if (!"minecraft".equals(requestItemName.getNamespace()) && !key.getNamespace().equals(requestItemName.getNamespace())) {
                    requestJson.addProperty("optional", true);
                }
                if (!"minecraft".equals(secondRequestItemName.getNamespace()) && !key.getNamespace().equals(secondRequestItemName.getNamespace())) {
                    offerJson.addProperty("optional", true);
                }
                trades.add(requestJson);
                trades.add(offerJson);
            }
            return DataResult.success(JsonOps.INSTANCE.convertTo(ops, obj));
        }

        @Override
        @SuppressWarnings({"unchecked", "rawtypes"})
        public <T> DataResult<Pair<Shop, T>> decode(DynamicOps<T> ops, T input) {
            JsonObject obj = ops.convertTo(JsonOps.INSTANCE, input).getAsJsonObject();

            String shopId = GsonHelper.getAsString(obj, "shopID");
            MutableComponent name = Component.translatable(GsonHelper.getAsString(obj, "name"));
            ShopOffers trades = new ShopOffers();
            if (obj.has("trades")) {
                for (JsonElement json : GsonHelper.getAsJsonArray(obj, "trades")) {
                    if (json.getAsJsonObject().has("offer") && json.getAsJsonObject().has("request")) {
                        ItemStack request = ItemAdapter.ITEM_READER.fromJson(json.getAsJsonObject().getAsJsonObject("request"), ItemStack.class);
                        ItemStack offer = ItemAdapter.ITEM_READER.fromJson(json.getAsJsonObject().getAsJsonObject("offer"), ItemStack.class);
                        trades.add(new ShopOffer(request, offer, 1, 1, 1));
                        if (json.getAsJsonObject().has("secondRequest")) {
                            ItemStack secondRequest = ItemAdapter.ITEM_READER.fromJson(json.getAsJsonObject().getAsJsonObject("secondRequest"), ItemStack.class);
                            trades.add(new ShopOffer(request, secondRequest, offer, 1, 1, 1));
                        }
                    }

                }
            }
            return DataResult.success(Pair.of(new Shop(shopId, name, trades), input));
        }

    }

}