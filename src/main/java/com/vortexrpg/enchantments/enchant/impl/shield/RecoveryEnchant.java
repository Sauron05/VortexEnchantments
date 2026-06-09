package com.vortexrpg.enchantments.enchant.impl.shield;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/** Recovery: Regen health slowly while blocking. */
public class RecoveryEnchant extends VortexEnchant {

    public RecoveryEnchant() { super("recovery", "Recovery", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.SHIELD)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        if (!player.isBlocking()) return;
        long tick = plugin.getServer().getCurrentTick();
        if (tick % 40 != 0) return;
        int duration = cfgi("duration", 40);
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, duration, level - 1, true, false));
    }

    @Override public String getDescription() { return "Regen while blocking."; }
    @Override public String getDescription(int level) {
        return "§7Block: §aRegeneration " + level + "§7 while blocking."; }
}
