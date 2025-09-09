package io.github.chakyl.societytrading.network;


import io.github.chakyl.societytrading.screen.SelectorMenu;
import io.github.chakyl.societytrading.screen.ShopMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;

import java.util.function.Supplier;

public class ServerBoundOpenShopMenuPacket {
    private final String shopID;

    public ServerBoundOpenShopMenuPacket(String shopID) {
        this.shopID = shopID;
    }

    public ServerBoundOpenShopMenuPacket(FriendlyByteBuf buffer) {
        this(buffer.readUtf());
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(this.shopID);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        ServerPlayer player = context.get().getSender();
        if (player != null) {
            if (player.containerMenu instanceof SelectorMenu menu && menu.stillValid(player)) {
//                player.openMenu(new SimpleMenuProvider((containerId, inventory, nPlayer) -> new ShopMenu(containerId, inventory, this.shopID), Component.translatable("shop.society_trading.selector.name")));
                NetworkHooks.openScreen(player, new SimpleMenuProvider((containerId, inventory, nPlayer) -> new ShopMenu(containerId, inventory, this.shopID), Component.translatable("shop.society_trading.selector.name")), buffer -> {
                    buffer.writeUtf(this.shopID);
                });
            }
        }
        context.get().setPacketHandled(true);
    }
}