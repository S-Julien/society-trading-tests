package io.github.chakyl.societytrading.util;

import dev.latvian.mods.kubejs.stages.Stages;
import io.github.chakyl.societytrading.SocietyTrading;
import io.github.chakyl.societytrading.data.Shop;
import io.github.chakyl.societytrading.trading.ShopOffer;
import io.github.chakyl.societytrading.trading.ShopOffers;
import net.minecraft.world.entity.player.Player;
import sereneseasons.api.season.SeasonHelper;

import java.util.ArrayList;
import java.util.Collection;

public class ShopData {

    public static Collection<Shop> getFilteredShops(Collection<Shop> shops, Player player) {
        Collection<Shop> newShops = new ArrayList<>();
        for (Shop shop : shops) {
            boolean flag = true;
            if (SocietyTrading.KUBEJS_INSTALLED) {
                if (!shop.stageRequired().isEmpty() && !Stages.get(player).has(shop.stageRequired())) {
                    flag = false;
                }
            }
            if (SocietyTrading.SERENE_SEASONS_INSTALLED) {
                if (!shop.seasonsRequired().isEmpty() && !shop.seasonsRequired().contains(SeasonHelper.getSeasonState(player.level()).getSubSeason().getSerializedName())) {
                    flag = false;
                }
            }
            if (flag) {
                newShops.add(shop);
            }
        }
        return newShops;
    }

    public static ShopOffers getFilteredTrades(ShopOffers trades, Player player) {
        ShopOffers newTrades = new ShopOffers();
        for (ShopOffer trade : trades) {
            if (trade.playerCanSee(player)) {
                newTrades.add(trade);
            }
        }
        return newTrades;
    }

    public static String formatPrice(String number) {
        if (number.length() < 4) return number;
        if (number.length() > 9) return number.charAt(0) + "." + number.charAt(1) + "B";
        if (number.length() > 6) {
            StringBuilder out = new StringBuilder(3);
            for (int i = 0; i < number.length() - 6 ; i++) {
                out.append(number.charAt(i));
            }
            if (number.length() == 7) {
                out.append('.');
                out.append(number.charAt(1));
            }
            out.append("M");
            return out.toString();
        }
        int start = number.length() % 3;
        StringBuilder out = new StringBuilder(number.length() + (number.length()/3));
        out.append(number, 0, start);
        for (int i = 0; i < number.length() / 3; i++) {
            if (i != 0 || start != 0) out.append(",");
            out.append(number, i*3 + start, i*3 + start + 3);
        }
        return out.toString();
    }

}