package io.github.chakyl.societytrading.screen;

import dev.shadowsoffire.placebo.reload.DynamicHolder;
import io.github.chakyl.societytrading.data.Shop;
import io.github.chakyl.societytrading.data.ShopRegistry;
import io.github.chakyl.societytrading.registry.ModElements;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.ClientSideMerchant;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.Merchant;

import java.util.Collection;

public class SelectorMenu extends AbstractContainerMenu {
    protected static final int PAYMENT1_SLOT = 0;
    protected static final int PAYMENT2_SLOT = 1;
    protected static final int RESULT_SLOT = 2;
    private static final int INV_SLOT_START = 3;
    private static final int INV_SLOT_END = 30;
    private static final int USE_ROW_SLOT_START = 30;
    private static final int USE_ROW_SLOT_END = 39;
    private static final int SELLSLOT1_X = 136;
    private static final int SELLSLOT2_X = 162;
    private static final int BUYSLOT_X = 220;
    private static final int ROW_Y = 37;
    private final Collection<Shop> shops;
    private int merchantLevel;
    private boolean showProgressBar;
    private boolean canRestock;

    public SelectorMenu(int pContainerId, Inventory pPlayerInventory) {
        this(pContainerId, pPlayerInventory, new ClientSideMerchant(pPlayerInventory.player));
    }

    public SelectorMenu(int pContainerId, Inventory pPlayerInventory, Merchant pTrader) {
        super(ModElements.Menus.SELECTOR_MENU.get(), pContainerId);
        this.shops = ShopRegistry.INSTANCE.getValues();
    }

    public Collection<Shop> getShops() {
        return this.shops;
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        return null;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return true;
    }
}