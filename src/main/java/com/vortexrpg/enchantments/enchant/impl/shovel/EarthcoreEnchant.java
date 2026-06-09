package com.vortexrpg.enchantments.enchant.impl.shovel;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.util.Vector;

import java.util.List;

/** Earthcore: After mining 30 blocks, ground eruption dealing massive AoE damage. */
public class EarthcoreEnchant extends VortexEnchant {

    public EarthcoreEnchant() { super("earthcore", "Earthcore", EnchantRarity.MYTHIC, 3, List.of(ItemTarget.SHOVEL)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        String key = "earthcore_count";
        int count = plugin.getPlayerDataManager().getInt(player.getUniqueId(), key) + 1;
        int threshold = cfgi("threshold", 30);
        if (count < threshold) {
            plugin.getPlayerDataManager().setInt(player.getUniqueId(), key, count);
            return;
        }
        plugin.getPlayerDataManager().setInt(player.getUniqueId(), key, 0);

        double radius = cfg("radius", 5.0 + level);
        double damage = cfg("damage", 8.0 + level * 4);
        Location loc = player.getLocation();

        // Eruption effects
        SoundUtil.play(loc, Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0f, 0.3f);
        ParticleUtil.burst(loc, Particle.LAVA, 40, radius);
        ParticleUtil.drawCircle(loc, radius, 60, Particle.FLAME);

        for (LivingEntity e : MathUtil.getNearbyLiving(loc, radius)) {
            if (e.equals(player)) continue;
            e.damage(damage, player);
            Vector knockback = e.getLocation().toVector().subtract(loc.toVector()).normalize().multiply(1.2);
            knockback.setY(0.8);
            e.setVelocity(e.getVelocity().add(knockback));
            e.setFireTicks(cfgi("fire-ticks", 60 + level * 20));
        }
    }

    @Override public String getDescription() { return "After 30 blocks, ground eruption deals massive AoE."; }
    @Override public String getDescription(int level) {
        return "§7Every §e30§7 blocks: §4eruption §c" + (int)(8 + level * 4) + "♥§7 + fire in " + (int)(5 + level) + "b."; }
}
