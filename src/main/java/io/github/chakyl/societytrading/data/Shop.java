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
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


/**
 * Stores all of the information representing a Shop.
 *
 * @param shopID             The unique ID of the shop
 * @param name               The display name of shop
 * @param texture            The resource location of the texture used for the shopkeeper
 * @param villagerProfession Villager Profession that enables the shop screen to show when interacted
 * @param entity             Living entity that opens the shop screen to show when interacted
 * @param blockTag           Block tag that opens the scree
 * @param hiddenFromSelector Shops is never shown on the Shop Selector list
 * @param jeiCatalyst        Item used as a catalyst in JEI
 * @param stageRequired      KubeJs stage the player needs to have to see the shop
 * @param stageOverride      KubeJs stage that always allows the player to see the shop
 * @param seasonsRequired    Serene Seasons season to display the shop
 * @param trades             Trade object
 */
public record Shop(String shopID, MutableComponent name, String texture, String villagerProfession,
                   EntityType<? extends LivingEntity> entity, TagKey<Block> blockTag, Boolean hiddenFromSelector,
                   ItemStack jeiCatalyst,
                   String stageRequired,
                   String stageOverride,
                   List<String> seasonsRequired, ShopOffers trades) implements CodecProvider<Shop> {

    public static final Codec<Shop> CODEC = new ShopCodec();
    public static final List<String> POSSIBLE_SEASONS = Arrays.asList("early_spring", "mid_spring", "late_spring", "early_summer", "mid_summer", "late_summer", "early_autumn", "mid_autumn", "late_autumn", "early_winter", "mid_winter", "late_winter");

    public Shop(Shop other) {
        this(other.shopID, other.name, other.texture, other.villagerProfession, other.entity, other.blockTag, other.hiddenFromSelector, other.jeiCatalyst, other.stageRequired, other.stageOverride, other.seasonsRequired, other.trades);
    }

    public int getColor() {
        return this.name.getStyle().getColor().getValue();
    }

    public Shop validate(ResourceLocation key) {
        Preconditions.checkNotNull(this.shopID, "Invalid shop ID!");
        Preconditions.checkNotNull(this.name, "Invalid shop name!");
        Preconditions.checkNotNull(this.texture, "Missing texture!");
        if (this.seasonsRequired != null) {
            this.seasonsRequired.forEach((season) -> {
                // Why is Java like that????
                if (!POSSIBLE_SEASONS.contains(season.replace("\"", ""))) {
                    throw new NullPointerException("Season " + season + " that doesn't exist! Possible values: " + POSSIBLE_SEASONS);
                }
            });
        }
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
            obj.addProperty("shop_id", input.shopID);
            obj.addProperty("name", ((TranslatableContents) input.name.getContents()).getKey());
            obj.addProperty("texture", input.texture);
            obj.addProperty("villager_profession", input.villagerProfession);
            obj.addProperty("entity", EntityType.getKey(input.entity).toString());
            if (input.blockTag != null) obj.addProperty("block_tag", input.blockTag.location().toString());
            obj.addProperty("hidden_from_selector", input.hiddenFromSelector);
            obj.add("jei_catalyst", ItemAdapter.ITEM_READER.toJsonTree(input.jeiCatalyst));
            obj.addProperty("stage_required", input.stageRequired);
            obj.addProperty("stage_override", input.stageOverride);
            JsonArray seasonsRequired = new JsonArray();
            obj.add("seasons_required", seasonsRequired);
            for (String season : input.seasonsRequired) {
                seasonsRequired.add(season.replace("\"", ""));
            }
            JsonArray trades = new JsonArray();
            obj.add("trades", trades);
            for (ShopOffer trade : input.trades) {
                JsonObject tradeObj = new JsonObject();
                JsonElement request = ItemAdapter.ITEM_READER.toJsonTree(trade.getCostA());
                JsonObject requestJson = request.getAsJsonObject();
                ResourceLocation requestItemName = new ResourceLocation(requestJson.get("item").getAsString());
                JsonElement secondRequest = ItemAdapter.ITEM_READER.toJsonTree(trade.getCostB());
                JsonObject secondRequestJson = secondRequest.getAsJsonObject();
                ResourceLocation secondRequestItemName = new ResourceLocation(secondRequestJson.get("item").getAsString());
                JsonElement offer = ItemAdapter.ITEM_READER.toJsonTree(trade.getResult());
                JsonObject offerJson = offer.getAsJsonObject();
                ResourceLocation offerItemName = new ResourceLocation(offerJson.get("item").getAsString());
                if (!"minecraft".equals(offerItemName.getNamespace()) && !key.getNamespace().equals(offerItemName.getNamespace())) {
                    offerJson.addProperty("optional", true);
                }
                if (!"minecraft".equals(requestItemName.getNamespace()) && !key.getNamespace().equals(requestItemName.getNamespace())) {
                    requestJson.addProperty("optional", true);
                }
                if (!"minecraft".equals(secondRequestItemName.getNamespace()) && !key.getNamespace().equals(secondRequestItemName.getNamespace())) {
                    secondRequestJson.addProperty("optional", true);
                }

                JsonArray tradeSeasonsRequired = new JsonArray();
                for (String season : trade.getSeasonsRequired()) {
                    tradeSeasonsRequired.add(season.replace("\"", ""));
                }
                tradeObj.add("request", requestJson);
                tradeObj.add("second_request", secondRequestJson);
                tradeObj.add("offer", offerJson);
                if (!Objects.equals(trade.getUnlockDescription(), Component.empty())) {
                    tradeObj.addProperty("unlock_description", ((TranslatableContents) trade.getUnlockDescription().getContents()).getKey());
                }
                tradeObj.addProperty("stage_required", trade.getStageRequired());
                tradeObj.addProperty("stage_override", trade.getStageOverride());
                tradeObj.add("seasons_required", tradeSeasonsRequired);
                tradeObj.addProperty("numismatics_cost", trade.getNumismaticsCost());
                trades.add(tradeObj);
            }
            return DataResult.success(JsonOps.INSTANCE.convertTo(ops, obj));
        }

        @Override
        @SuppressWarnings({"unchecked", "rawtypes"})
        public <T> DataResult<Pair<Shop, T>> decode(DynamicOps<T> ops, T input) {
            JsonObject obj = ops.convertTo(JsonOps.INSTANCE, input).getAsJsonObject();
            String shopId = GsonHelper.getAsString(obj, "shop_id");
            MutableComponent name = Component.translatable(GsonHelper.getAsString(obj, "name"));
            String texture = GsonHelper.getAsString(obj, "texture");
            String villagerProfession = "";
            if (obj.has("villager_profession")) {
                villagerProfession = GsonHelper.getAsString(obj, "villager_profession");
            }
            EntityType<? extends LivingEntity> entity = null;
            if (obj.has("entity")) {
                String entityStr = GsonHelper.getAsString(obj, "entity");
                entity = (EntityType) ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(entityStr));
                if (entity == EntityType.PIG && !"minecraft:pig".equals(entityStr))
                    throw new JsonParseException("Shop has entity type " + entityStr);
            }
            TagKey<Block> blockTag = null;
            if (obj.has("block_tag")) {
                blockTag = TagKey.create(Registries.BLOCK, new ResourceLocation(obj.get("block_tag").getAsString()));
            }
            boolean hiddenFromSelector = false;
            if (obj.has("hidden_from_selector")) {
                hiddenFromSelector = GsonHelper.getAsBoolean(obj, "hidden_from_selector");
            }
            ItemStack jeiCatalyst = Items.VILLAGER_SPAWN_EGG.getDefaultInstance();
            if (obj.has("jei_catalyst")) {
                jeiCatalyst = ItemAdapter.ITEM_READER.fromJson(obj.getAsJsonObject("jei_catalyst"), ItemStack.class);
            }
            String stageRequired = "";
            if (obj.has("stage_required")) {
                stageRequired = GsonHelper.getAsString(obj, "stage_required");
            }
            String stageOverride = "";
            if (obj.has("stage_override")) {
                stageOverride = GsonHelper.getAsString(obj, "stage_override");
            }
            List<String> seasonsRequired = new ArrayList<>();
            if (obj.has("seasons_required")) {
                for (JsonElement json : GsonHelper.getAsJsonArray(obj, "seasons_required")) {
                    seasonsRequired.add(String.valueOf(json).replace("\"", ""));
                }
            }
            ShopOffers trades = new ShopOffers();
            if (obj.has("trades")) {
                for (JsonElement json : GsonHelper.getAsJsonArray(obj, "trades")) {
                    if (json.getAsJsonObject().has("offer") && json.getAsJsonObject().has("request")) {
                        ItemStack request = ItemAdapter.ITEM_READER.fromJson(json.getAsJsonObject().getAsJsonObject("request"), ItemStack.class);
                        ItemStack offer = ItemAdapter.ITEM_READER.fromJson(json.getAsJsonObject().getAsJsonObject("offer"), ItemStack.class);
                        String tradeStage = "";
                        String tradeStageOverride = "";
                        int numismaticsCost = 0;
                        List<String> tradeSeasonsRequired = new ArrayList<>();
                        if (json.getAsJsonObject().has("seasons_required")) {
                            for (JsonElement arrayJson : GsonHelper.getAsJsonArray(json.getAsJsonObject(), "seasons_required")) {
                                tradeSeasonsRequired.add(String.valueOf(arrayJson).replace("\"", ""));
                            }
                        }
                        MutableComponent tradeUnlockDescription = Component.empty();
                        if (json.getAsJsonObject().has("unlock_description")) {
                            tradeUnlockDescription = Component.translatable(GsonHelper.getAsString(json.getAsJsonObject(), "unlock_description"));
                        }
                        if (json.getAsJsonObject().has("stage_required"))
                            tradeStage = GsonHelper.getAsString(json.getAsJsonObject(), "stage_required");
                        if (json.getAsJsonObject().has("stage_override"))
                            tradeStageOverride = GsonHelper.getAsString(json.getAsJsonObject(), "stage_override");
                        if (json.getAsJsonObject().has("numismatics_cost"))
                            numismaticsCost = GsonHelper.getAsInt(json.getAsJsonObject(), "numismatics_cost");
                        if (json.getAsJsonObject().has("second_request")) {
                            ItemStack secondRequest = ItemAdapter.ITEM_READER.fromJson(json.getAsJsonObject().getAsJsonObject("second_request"), ItemStack.class);
                            trades.add(new ShopOffer(request, secondRequest, offer, tradeUnlockDescription, tradeStage, tradeStageOverride, tradeSeasonsRequired, numismaticsCost));
                        } else {
                            trades.add(new ShopOffer(request, offer, tradeUnlockDescription, tradeStage, tradeStageOverride, tradeSeasonsRequired, numismaticsCost));
                        }
                    }

                }
            }
            return DataResult.success(Pair.of(new Shop(shopId, name, texture, villagerProfession, entity, blockTag, hiddenFromSelector, jeiCatalyst, stageRequired, stageOverride, seasonsRequired, trades), input));
        }

    }

}