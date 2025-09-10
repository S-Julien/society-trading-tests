package io.github.chakyl.societytrading;

import dev.shadowsoffire.placebo.tabs.TabFillingRegistry;
import io.github.chakyl.societytrading.data.ShopRegistry;
import io.github.chakyl.societytrading.registry.ModElements;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod(SocietyTrading.MODID)
public class SocietyTrading {
    public static final String MODID = "society_trading";
    public static final Logger LOGGER = LogManager.getLogger(MODID);
    public static boolean SERENE_SEASONS_INSTALLED = false;
    public static boolean KUBEJS_INSTALLED = false;
    public static boolean NUMISMATICS_INSTALLED = false;
    public static boolean NUMISMATICS_UTILS_INSTALLED = false;
    public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(MODID, MODID))
            .clientAcceptedVersions(s -> true)
            .serverAcceptedVersions(s -> true)
            .networkProtocolVersion(() -> "1.0.0")
            .simpleChannel();

    public SocietyTrading() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.register(this);
//        SlimyConfig.load();
        ModElements.bootstrap();
//        MessageHelper.registerMessage(CHANNEL, 0, new SlimyConfig.ConfigMessage.Provider());
    }

    @SubscribeEvent
    public void setup(FMLCommonSetupEvent e) {
        e.enqueueWork(() -> {
            TabFillingRegistry.register(ModElements.Tabs.TAB_KEY, ModElements.Items.SHOP_BLOCK);
        });
        ShopRegistry.INSTANCE.registerToBus();
    }

    public static ResourceLocation loc(String path) {
        return new ResourceLocation(MODID, path);
    }
}