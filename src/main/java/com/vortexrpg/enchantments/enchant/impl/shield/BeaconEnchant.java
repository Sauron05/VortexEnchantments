package com.vortexrpg.enchantments.enchant.impl.shield;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;

import java.util.List;

/** Beacon: While holding shield, nearby allies (players within 12 blocks) gain Regeneration I. */
public class BeaconEnchant extends VortexEnchant {
    public BeaconEnchant() { super("beacon", "Beacon", EnchantRarity.EPIC, 3, List.of(ItemTarget.SHIELD)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        long tick = plugin.getServer().getCurrentTick();
        if (tick % 40 != 0) return;
        int r = cfgi("radius", 10 + level*2);
        player.getWorld().getNearbyEntities(player.getLocation(), r, r, r,
            e -> e instanceof Player && e != player).forEach(e -> {
            Player ally = (Player) e;
            if (!ally.hasPotionEffect(org.bukkit.potion.PotionEffectType.REGENERATION)) {
                ally.addPotionEffect(new org.bukkit.potion.PotionEffect(
                    org.bukkit.potion.PotionEffectType.REGENERATION, 60, level - 1, true, false, false));
            }
        });
    }

    @Override public String getDescription() { return "Radiates Regeneration to nearby allies."; }
    @Override public String getDescription(int level) {
        return "§7Nearby allies: §aRegen " + level + "§7 within §a" + (10+level*2) + "§7 blocks."; }
}
