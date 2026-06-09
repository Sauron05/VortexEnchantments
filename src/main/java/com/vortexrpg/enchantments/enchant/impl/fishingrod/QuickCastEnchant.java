package com.vortexrpg.enchantments.enchant.impl.fishingrod;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/** QuickCast: Grants temporary Lure effect on cast to speed up fishing. */
public class QuickCastEnchant extends VortexEnchant {

    public QuickCastEnchant() { super("quick_cast", "Quick Cast", EnchantRarity.COMMON, 3, List.of(ItemTarget.FISHING_ROD)); }

    @Override
    public void onFish(PlayerFishEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (event.getState() == PlayerFishEvent.State.FISHING) {
            int duration = cfgi("duration", 10 + level * 5) * 20;
            player.addPotionEffect(new PotionEffect(PotionEffectType.LUCK, duration, level - 1, true, false, true));
        }
    }

    @Override public String getDescription() { return "Faster fishing bites."; }
    @Override public String getDescription(int level) {
        return "§7Casting grants §aLuck§7 for §e" + (10 + level * 5) + "s§7 to speed catches."; }
}
