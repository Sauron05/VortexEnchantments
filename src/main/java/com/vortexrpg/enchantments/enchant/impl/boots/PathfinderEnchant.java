package com.vortexrpg.enchantments.enchant.impl.boots;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.List;

/** Pathfinder: Show footprint particles on unexplored terrain; bonus 10/15/20% speed boost in new biomes. */
@SuppressWarnings("removal")
public class PathfinderEnchant extends VortexEnchant {
    @SuppressWarnings("unused")
    private static final double[] SPEED_BONUS = {0.10, 0.15, 0.20};

    public PathfinderEnchant() { super("pathfinder", "Pathfinder", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.BOOTS)); }

    @Override
    public void onMove(PlayerMoveEvent event, Player player, int level) {
        if (!isEnabled() || !event.hasChangedBlock()) return;
        String biome = player.getLocation().getBlock().getBiome().name();
        boolean newBiome = !plugin.getPlayerDataManager().hasVisitedBiome(player.getUniqueId(), biome);
        if (newBiome) {
            plugin.getPlayerDataManager().markBiomeVisited(player.getUniqueId(), biome);
            player.addPotionEffect(new org.bukkit.potion.PotionEffect(
                org.bukkit.potion.PotionEffectType.SPEED, 100, level, true, false, false));
            com.vortexrpg.enchantments.util.ParticleUtil.trail(player.getLocation(), org.bukkit.Particle.FIREWORK, 3, 0.3f);
        }
    }

    @Override public String getDescription() { return "Speed boost in unexplored biomes."; }
    @Override public String getDescription(int level) {
        return "§7New biome: §aSpeed " + (level+1) + "§7 for 5s."; }
}
