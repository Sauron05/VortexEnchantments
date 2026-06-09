package com.vortexrpg.enchantments.enchant.impl.shield;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

import java.util.List;

/** Paragon: Every N blocks triggers massive force explosion pushing all mobs. */
public class ParagonEnchant extends VortexEnchant {

    public ParagonEnchant() { super("paragon", "Paragon", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.SHIELD)); }

    @Override
    public void onDamageTaken(EntityDamageByEntityEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!player.isBlocking()) return;
        String key = "paragon_blocks";
        int count = plugin.getPlayerDataManager().getInt(player.getUniqueId(), key) + 1;
        int threshold = cfgi("threshold", 12 - level * 2);
        if (count < threshold) {
            plugin.getPlayerDataManager().setInt(player.getUniqueId(), key, count);
            return;
        }
        plugin.getPlayerDataManager().setInt(player.getUniqueId(), key, 0);
        double radius = cfg("radius", 6.0 + level * 2);
        double force = cfg("force", 1.0 + level * 0.5);
        for (LivingEntity e : MathUtil.getNearbyLiving(player.getLocation(), radius)) {
            if (e.equals(player)) continue;
            Vector push = e.getLocation().toVector().subtract(player.getLocation().toVector()).normalize().multiply(force).setY(0.5);
            e.setVelocity(push);
        }
        SoundUtil.play(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 0.8f);
        ParticleUtil.burst(player.getLocation(), Particle.EXPLOSION, 10, radius);
    }

    @Override public String getDescription() { return "Every 10 blocks: force explosion."; }
    @Override public String getDescription(int level) {
        return "§7Every §e" + (12 - level * 2) + "§7 blocks: §6massive shockwave§7 in " + (int)(6 + level * 2) + "b."; }
}
