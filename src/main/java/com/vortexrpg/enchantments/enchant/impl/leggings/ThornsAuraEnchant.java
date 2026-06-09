package com.vortexrpg.enchantments.enchant.impl.leggings;

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

import java.util.List;

/**
 * ThornsAura: On kill, deal damage to nearby enemies in a splash.
 */
public class ThornsAuraEnchant extends VortexEnchant {
    public ThornsAuraEnchant() { super("thorns_aura", "Thorns Aura", EnchantRarity.EPIC, 3, List.of(ItemTarget.LEGGINGS)); }

    @Override
    public void onKill(EntityDamageByEntityEvent event, Player player, LivingEntity killed, int level) {
        if (!isEnabled()) return;
        double radius = cfgd("radius", 4.0);
        double dmg = cfgd("splash_damage", 1.5 * level);
        for (LivingEntity e : MathUtil.getNearbyLiving(killed.getLocation(), radius)) {
            if (e.equals(player) || e.equals(killed)) continue;
            e.damage(dmg, player);
        }
        ParticleUtil.burst(killed.getLocation().add(0, 1, 0), Particle.DAMAGE_INDICATOR, 12, radius);
        SoundUtil.play(killed.getLocation(), Sound.ENCHANT_THORNS_HIT, 0.7f, 0.8f);
    }

    @Override public String getDescription(int level) {
        return "§7On kill: deal §c" + String.format("%.1f", 1.5 * level) + " §7splash damage in 4 blocks.";
    }
}
