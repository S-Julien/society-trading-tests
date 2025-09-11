package io.github.chakyl.societytrading.network;

import io.github.chakyl.societytrading.SocietyTrading;
import io.github.chakyl.societytrading.screen.ShopMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;


import java.util.function.Supplier;

public class ClientBoundBalancePacket {
    private final int balance;

    public ClientBoundBalancePacket(int balance) {
        this.balance = balance;
    }

    public ClientBoundBalancePacket(FriendlyByteBuf buffer) {
        this(buffer.readInt());
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(this.balance);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            if (player.containerMenu instanceof ShopMenu menu && menu.stillValid(player)) {
                SocietyTrading.LOGGER.info(this.balance);
                menu.setPlayerBalance(this.balance);
                menu.broadcastChanges();
            }
        }
        context.get().setPacketHandled(true);

    }
}