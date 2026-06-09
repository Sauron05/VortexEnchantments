package com.vortexrpg.enchantments.enchant.impl.chestplate;

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
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.List;

/**
 * Martyrdom: On lethal damage, schedule an AoE explosion at death location.
 */
public class MartyrdomEnchant extends VortexEnchant {
    public MartyrdomEnchant() { super("martyrdom", "Martyrdom", EnchantRarity.EPIC, 3, List.of(ItemTarget.CHESTPLATE)); }

    @Override
    public void onDamageTaken(EntityDamageEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (player.getHealth() - event.getFinalDamage() > 0) return;

        double radius = cfgd("radius", 5.0 + level * 2.0);
        double dmg = cfgd("explosion_damage", 4.0 + level * 3.0);
        Location loc = player.getLocation().clone();

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            for (LivingEntity e : MathUtil.getNearbyLiving(loc, radius)) {
                if (e.equals(player)) continue;
                e.damage(dmg);
            }
            ParticleUtil.burst(loc, Particle.EXPLOSION, 5, 2.0);
            SoundUtil.play(loc, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 0.8f);
        }, 1L);
    }

    @Override public String getDescription(int level) {
        return "§7On death: §cexplode §7for " + (int)(4 + level * 3) + " damage in " + (int)(5 + level * 2) + " blocks.";
    }
}
