package com.vortexrpg.enchantments.enchant.impl.helmet;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.util.List;

/**
 * Prism: Sneak to cycle through 3 aura modes (damage/defense/speed).
 * Active mode gives passive buff.
 */
public class PrismEnchant extends VortexEnchant {
    private static final java.util.Map<java.util.UUID, Integer> MODE = new java.util.HashMap<>();

    public PrismEnchant() { super("prism", "Prism", EnchantRarity.RARE, 3, List.of(ItemTarget.HELMET)); }

    @Override
    public void onToggleSneak(PlayerToggleSneakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!event.isSneaking()) return;
        int mode = MODE.merge(player.getUniqueId(), 1, (a, b) -> (a + 1) % 3);
        String[] names = {"§cDamage", "§bDefense", "§aSpeed"};
        player.sendActionBar(net.kyori.adventure.text.Component.text("§7Prism Mode: " + names[mode]));
    }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        int mode = MODE.getOrDefault(player.getUniqueId(), 0);
        int dur = 60;
        switch (mode) {
            case 0 -> player.addPotionEffect(new org.bukkit.potion.PotionEffect(
                    org.bukkit.potion.PotionEffectType.STRENGTH, dur, level - 1, true, false, false));
            case 1 -> player.addPotionEffect(new org.bukkit.potion.PotionEffect(
                    org.bukkit.potion.PotionEffectType.RESISTANCE, dur, level - 1, true, false, false));
            case 2 -> player.addPotionEffect(new org.bukkit.potion.PotionEffect(
                    org.bukkit.potion.PotionEffectType.SPEED, dur, level - 1, true, false, false));
        }
    }

    @Override public String getDescription(int level) {
        return "§7Sneak to cycle: §cDamage§7/§bDefense§7/§aSpeed §7aura.";
    }
}
