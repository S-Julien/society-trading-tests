package io.github.chakyl.societytrading.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import io.github.chakyl.societytrading.data.Shop;
import io.github.chakyl.societytrading.data.ShopRegistry;
import io.github.chakyl.societytrading.screen.ShopMenu;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraftforge.network.NetworkHooks;

public class OpenShopCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("openshop")
                .requires(source -> source.hasPermission(2)) // Requires OP level 2 (admin)
                .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("shopID", StringArgumentType.string())
                                .executes(OpenShopCommand::execute)
                        )
                )
        );
    }

    private static int execute(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer targetPlayer = EntityArgument.getPlayer(context, "player");
        String shopID = StringArgumentType.getString(context, "shopID");
        CommandSourceStack source = context.getSource();

        try {
            DynamicHolder<Shop> shopHolder = ShopRegistry.INSTANCE.holder(new ResourceLocation("society_trading:" + shopID));
            Shop shop = shopHolder.get();

            if (shop == null) {
                source.sendFailure(Component.literal("Shop with ID '" + shopID + "' not found!"));
                return 0;
            }

            NetworkHooks.openScreen(targetPlayer, new SimpleMenuProvider(
                    (containerId, inventory, player) -> new ShopMenu(containerId, inventory, shopID),
                    shop.name()
            ), buffer -> {
                buffer.writeUtf(shopID);
            });

            source.sendSuccess(() -> Component.literal("Opened shop '" + shopID + "' for " + targetPlayer.getName().getString()), true);
            return 1;

        } catch (Exception e) {
            source.sendFailure(Component.literal("Failed to open shop: " + e.getMessage()));
            return 0;
        }
    }
}
