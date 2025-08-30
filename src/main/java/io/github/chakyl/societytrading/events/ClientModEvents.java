package io.github.chakyl.societytrading.events;

import dev.shadowsoffire.placebo.reload.DynamicHolder;
import io.github.chakyl.societytrading.SocietyTrading;
import io.github.chakyl.societytrading.registry.ModElements;
import io.github.chakyl.societytrading.screen.ShopMenu;
import io.github.chakyl.societytrading.screen.ShopScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.Set;


@EventBusSubscriber(bus = Bus.MOD, value = Dist.CLIENT, modid = SocietyTrading.MODID)
public class ClientModEvents {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(ModElements.Menus.SHOP_MENU.get(), ShopScreen::new);
        });

    }

}