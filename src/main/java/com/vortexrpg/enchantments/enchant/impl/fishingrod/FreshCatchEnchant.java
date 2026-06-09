package com.vortexrpg.enchantments.enchant.impl.fishingrod;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/** FreshCatch: Saturation bonus when catching fish. */
public class FreshCatchEnchant extends VortexEnchant {

    public FreshCatchEnchant() { super("fresh_catch", "Fresh Catch", EnchantRarity.COMMON, 3, List.of(ItemTarget.FISHING_ROD)); }

    @Override
    public void onFish(PlayerFishEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;
        int duration = cfgi("duration", 1 + level) * 20;
        player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, duration, 0, true, false, true));
    }

    @Override public String getDescription() { return "Saturation on catching fish."; }
    @Override public String getDescription(int level) {
        return "§7Catching fish grants §aSaturation§7 for §e" + (1 + level) + "s§7."; }
}
