package com.vortexrpg.enchantments.enchant.impl.shovel;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.List;

/** Terra Siphon: Mining soft blocks heals and restores hunger. */
public class TerraSiphonEnchant extends VortexEnchant {

    public TerraSiphonEnchant() { super("terra_siphon", "Terra Siphon", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.SHOVEL)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        Material mat = event.getBlock().getType();
        if (mat != Material.DIRT && mat != Material.GRASS_BLOCK && mat != Material.SAND
                && mat != Material.GRAVEL && mat != Material.CLAY && mat != Material.MUD
                && mat != Material.SOUL_SAND && mat != Material.SOUL_SOIL) return;
        double healAmount = cfg("heal", 0.5 * level);
        int foodRestore = cfgi("food-restore", level);
        double maxHealth = player.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue();
        player.setHealth(Math.min(maxHealth, player.getHealth() + healAmount));
        player.setFoodLevel(Math.min(20, player.getFoodLevel() + foodRestore));
        ParticleUtil.spawn(player.getLocation().add(0, 1, 0), Particle.HEART, 3, 0.5);
    }

    @Override public String getDescription() { return "Mining soft blocks heals and feeds you."; }
    @Override public String getDescription(int level) {
        return "§7Mine soft blocks: §c+" + String.format("%.1f", 0.5 * level) + "♥§7 + §a+" + level + "🍗§7."; }
}
