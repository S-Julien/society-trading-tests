package io.github.chakyl.societytrading.data;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import dev.shadowsoffire.placebo.reload.DynamicRegistry;
import io.github.chakyl.societytrading.SocietyTrading;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.HashMap;
import java.util.Map;

public class ShopRegistry extends DynamicRegistry<Shop> {

    public static final ShopRegistry INSTANCE = new ShopRegistry();

    private Map<String, Shop> shopsByID = new HashMap<>();

    public ShopRegistry() {
        super(SocietyTrading.LOGGER, "shops", true, false);
    }

    @Override
    protected void registerBuiltinCodecs() {
        this.registerDefaultCodec(new ResourceLocation(SocietyTrading.MODID, "shops"), Shop.CODEC);
    }

    @Override
    protected void beginReload() {
        super.beginReload();
        this.shopsByID = new HashMap<>();
    }

    @Override
    protected void onReload() {
        super.onReload();
        this.shopsByID = ImmutableMap.copyOf(this.shopsByID);
    }

    @Override
    protected void validateItem(ResourceLocation key, Shop shop) {
        shop.validate(key);
        if (this.shopsByID.containsKey(shop.shopID())) {
            String msg = "Attempted to register two shops (%s and %s) with the same shopID: %s!";
            throw new UnsupportedOperationException(String.format(msg, key, this.getKey(this.shopsByID.get(shop.shopID())), shop.shopID()));
        }
        this.shopsByID.put(shop.shopID(), shop);
    }

    @Override
    public Map<ResourceLocation, JsonElement> prepare(ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        return super.prepare(pResourceManager, pProfiler);
    }

}