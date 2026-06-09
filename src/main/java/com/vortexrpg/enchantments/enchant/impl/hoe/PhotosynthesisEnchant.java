package com.vortexrpg.enchantments.enchant.impl.hoe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;

import java.util.List;

/** Photosynthesis: Standing in sunlight while holding hoe regens health. */
public class PhotosynthesisEnchant extends VortexEnchant {

    public PhotosynthesisEnchant() { super("photosynthesis", "Photosynthesis", EnchantRarity.EPIC, 3, List.of(ItemTarget.HOE)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        long tick = plugin.getServer().getCurrentTick();
        if (tick % 40 != 0) return;
        long time = player.getWorld().getTime();
        if (time >= 12000) return; // Night
        if (player.getLocation().getBlock().getLightFromSky() < 15) return;
        double heal = cfg("heal", 0.25 * level);
        double maxHealth = player.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue();
        player.setHealth(Math.min(maxHealth, player.getHealth() + heal));
    }

    @Override public String getDescription() { return "Sunlight heals you while holding hoe."; }
    @Override public String getDescription(int level) {
        return "§7Sunlight: regen §c" + String.format("%.2f", 0.25 * level) + "♥§7/2s."; }
}
