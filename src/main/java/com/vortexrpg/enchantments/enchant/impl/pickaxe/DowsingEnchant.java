package com.vortexrpg.enchantments.enchant.impl.pickaxe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;
import java.util.Set;

/** Dowsing: Hold right-click to detect nearest ore vein within 12/16/20 blocks via particles. */
public class DowsingEnchant extends VortexEnchant {
    private static final double[] RADIUS = {12, 16, 20};
    private static final Set<Material> TARGET_ORES = Set.of(
        Material.COAL_ORE, Material.IRON_ORE, Material.GOLD_ORE, Material.LAPIS_ORE,
        Material.REDSTONE_ORE, Material.DIAMOND_ORE, Material.EMERALD_ORE,
        Material.DEEPSLATE_COAL_ORE, Material.DEEPSLATE_IRON_ORE, Material.DEEPSLATE_GOLD_ORE,
        Material.DEEPSLATE_LAPIS_ORE, Material.DEEPSLATE_REDSTONE_ORE, Material.DEEPSLATE_DIAMOND_ORE,
        Material.DEEPSLATE_EMERALD_ORE
    );

    public DowsingEnchant() { super("dowsing", "Dowsing", EnchantRarity.RARE, 3, List.of(ItemTarget.PICKAXE)); }

    @Override
    public void onInteract(PlayerInteractEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (isOnCooldown(player)) return;
        setCooldownSeconds(player, 3);
        double radius = cfg("detection_radius", RADIUS[level-1]);
        Location origin = player.getLocation();
        Block nearest = null;
        double nearestDist = Double.MAX_VALUE;
        int r = (int) radius;
        for (int x = -r; x <= r; x++) {
            for (int y = -r; y <= r; y++) {
                for (int z = -r; z <= r; z++) {
                    Block b = origin.clone().add(x, y, z).getBlock();
                    if (!TARGET_ORES.contains(b.getType())) continue;
                    double d = b.getLocation().distanceSquared(origin);
                    if (d < nearestDist) { nearestDist = d; nearest = b; }
                }
            }
        }
        if (nearest == null) return;
        Location target = nearest.getLocation().add(0.5, 0.5, 0.5);
        // Draw particles along path
        for (double t = 0; t < 1.0; t += 0.05) {
            Location pt = origin.clone().add(target.toVector().subtract(origin.toVector()).multiply(t));
            origin.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, pt, 1, 0, 0, 0, 0);
        }
    }

    @Override public String getDescription() { return "Right-click to dowse for nearest ore vein."; }
    @Override public String getDescription(int level) {
        return "§7§eRight-click§7: particle trail toward nearest ore within §a" + (int)RADIUS[level-1] + " blocks§7."; }
}
