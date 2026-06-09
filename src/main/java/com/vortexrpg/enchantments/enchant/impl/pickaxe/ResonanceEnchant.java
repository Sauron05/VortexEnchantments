package com.vortexrpg.enchantments.enchant.impl.pickaxe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.List;

/** Resonance: Mining ore pulses revealing matching ore within 6/8/10 blocks via Glowing for 3s. */
public class ResonanceEnchant extends VortexEnchant {
    private static final double[] RADIUS = {6, 8, 10};

    public ResonanceEnchant() { super("resonance", "Resonance", EnchantRarity.RARE, 3, List.of(ItemTarget.PICKAXE)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!event.getBlock().getType().name().endsWith("_ORE")) return;
        double radius = cfg("radius", RADIUS[level-1]);
        Location center = event.getBlock().getLocation();
        int r = (int) radius;
        for (int x = -r; x <= r; x++) {
            for (int y = -r; y <= r; y++) {
                for (int z = -r; z <= r; z++) {
                    org.bukkit.block.Block b = center.clone().add(x, y, z).getBlock();
                    if (b.getType() == event.getBlock().getType()) {
                        // Highlight via glowing block display entity or fallback to particle burst
                        // We spawn a short-lived glowing shulker bullet cluster at each ore
                        org.bukkit.entity.ShulkerBullet sb = (org.bukkit.entity.ShulkerBullet)
                            center.getWorld().spawnEntity(b.getLocation().add(0.5, 0.5, 0.5), org.bukkit.entity.EntityType.SHULKER_BULLET);
                        sb.setGlowing(true);
                        sb.setVelocity(new org.bukkit.util.Vector(0, 0, 0));
                        plugin.getServer().getScheduler().runTaskLater(plugin, sb::remove, 60L);
                    }
                }
            }
        }
    }

    @Override public String getDescription() { return "Mining ore pulses to reveal nearby matching ores."; }
    @Override public String getDescription(int level) {
        return "§7Mining ore: reveals matching ores within §a" + (int)RADIUS[level-1] + " blocks§7 for §e3s§7."; }
}
