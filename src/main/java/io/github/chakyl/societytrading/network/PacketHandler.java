package io.github.chakyl.societytrading.network;

import io.github.chakyl.societytrading.SocietyTrading;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import org.apache.logging.log4j.core.jmx.Server;

import java.util.function.Supplier;

public class PacketHandler {
    private static final SimpleChannel INSTANCE = NetworkRegistry.ChannelBuilder.named(
                    new ResourceLocation(SocietyTrading.MODID, "main"))
            .serverAcceptedVersions((version) -> true)
            .clientAcceptedVersions((version) -> true)
            .networkProtocolVersion(() -> String.valueOf(1))
            .simpleChannel();

    public static void register() {
        INSTANCE.messageBuilder(ServerBoundTradeButtonClickPacket.class, 0, NetworkDirection.PLAY_TO_SERVER)
                .encoder(ServerBoundTradeButtonClickPacket::encode)
                .decoder(ServerBoundTradeButtonClickPacket::new)
                .consumerMainThread(ServerBoundTradeButtonClickPacket::handle)
                .add();

        INSTANCE.messageBuilder(ServerBoundOpenShopMenuPacket.class, 1, NetworkDirection.PLAY_TO_SERVER)
                .encoder(ServerBoundOpenShopMenuPacket::encode)
                .decoder(ServerBoundOpenShopMenuPacket::new)
                .consumerMainThread(ServerBoundOpenShopMenuPacket::handle)
                .add();

    }

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.send(PacketDistributor.SERVER.noArg(), message);
    }


}