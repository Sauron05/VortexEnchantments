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

/** Annihilation Wall: After blocking N hits, release massive damage wave. */
public class AnnihilationWallEnchant extends VortexEnchant {

    public AnnihilationWallEnchant() { super("annihilation_wall", "Annihilation Wall", EnchantRarity.MYTHIC, 3, List.of(ItemTarget.SHIELD)); }

    @Override
    public void onDamageTaken(EntityDamageByEntityEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!player.isBlocking()) return;
        String key = "annihilation_blocks";
        int count = plugin.getPlayerDataManager().getInt(player.getUniqueId(), key) + 1;
        int threshold = cfgi("threshold", 12 - level * 2);
        if (count < threshold) {
            plugin.getPlayerDataManager().setInt(player.getUniqueId(), key, count);
            return;
        }
        plugin.getPlayerDataManager().setInt(player.getUniqueId(), key, 0);
        double radius = cfg("radius", 6.0 + level * 2);
        double damage = cfg("wave-damage", 4.0 + level * 3);
        for (LivingEntity e : MathUtil.getNearbyLiving(player.getLocation(), radius)) {
            if (e.equals(player)) continue;
            e.damage(damage, player);
            Vector push = e.getLocation().toVector().subtract(player.getLocation().toVector()).normalize().multiply(1.5).setY(0.5);
            e.setVelocity(push);
        }
        SoundUtil.play(player.getLocation(), Sound.ENTITY_WARDEN_SONIC_BOOM, 1.0f, 0.6f);
        ParticleUtil.burst(player.getLocation(), Particle.EXPLOSION, 15, radius);
    }

    @Override public String getDescription() { return "After blocking hits: massive damage wave."; }
    @Override public String getDescription(int level) {
        return "§7After §e" + (12 - level * 2) + "§7 blocks: §c" + (int)(4 + level * 3) + "♥§7 AoE wave in " + (int)(6 + level * 2) + "b."; }
}
