package com.vortexrpg.enchantments.enchant.impl.shovel;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.util.Vector;

import java.util.List;

/** Cataclysm Shovel: Periodic sinkhole creating AoE damage + pull. */
public class CataclysmShovelEnchant extends VortexEnchant {

    public CataclysmShovelEnchant() { super("cataclysm_shovel", "Cataclysm Shovel", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.SHOVEL)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (isOnCooldown(player)) return;
        String key = "cataclysm_count";
        int count = plugin.getPlayerDataManager().getInt(player.getUniqueId(), key) + 1;
        int threshold = cfgi("threshold", 15);
        if (count < threshold) {
            plugin.getPlayerDataManager().setInt(player.getUniqueId(), key, count);
            return;
        }
        plugin.getPlayerDataManager().setInt(player.getUniqueId(), key, 0);

        int radius = cfgi("radius", 3 + level);
        double damage = cfg("damage", 5.0 + level * 2);
        Block center = event.getBlock();
        Location loc = center.getLocation().add(0.5, 0.5, 0.5);

        // Create sinkhole (remove top layers)
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (x * x + z * z > radius * radius) continue;
                for (int y = 0; y >= -2; y--) {
                    Block b = center.getRelative(x, y, z);
                    if (b.getType() != Material.BEDROCK && !b.getType().isAir()) {
                        b.setType(Material.AIR);
                    }
                }
            }
        }

        SoundUtil.play(loc, Sound.ENTITY_WARDEN_EMERGE, 1.0f, 0.5f);
        ParticleUtil.drawCircle(loc, radius, 50, Particle.CAMPFIRE_COSY_SMOKE);

        for (LivingEntity e : MathUtil.getNearbyLiving(loc, radius + 2)) {
            if (e.equals(player)) continue;
            e.damage(damage, player);
            Vector pull = loc.toVector().subtract(e.getLocation().toVector()).normalize().multiply(0.5);
            pull.setY(-0.5);
            e.setVelocity(e.getVelocity().add(pull));
        }
        setCooldownFromConfig(player, "cooldown", 20);
    }

    @Override public String getDescription() { return "Periodic sinkhole with AoE damage."; }
    @Override public String getDescription(int level) {
        return "§7Every 15 blocks: sinkhole + §c" + (int)(5 + level * 2) + "♥§7 + pull in " + (3 + level) + "b."; }
}
