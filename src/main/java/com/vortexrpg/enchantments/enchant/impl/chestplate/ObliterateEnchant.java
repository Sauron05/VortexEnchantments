package com.vortexrpg.enchantments.enchant.impl.chestplate;

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
 * Obliterate: On kill, deal overkill damage as AoE to nearby enemies.
 */
public class ObliterateEnchant extends VortexEnchant {
    public ObliterateEnchant() { super("obliterate", "Obliterate", EnchantRarity.MYTHIC, 3, List.of(ItemTarget.CHESTPLATE)); }

    @Override
    public void onKill(EntityDamageByEntityEvent event, Player player, LivingEntity killed, int level) {
        if (!isEnabled()) return;
        double overkill = event.getFinalDamage() - killed.getHealth();
        if (overkill <= 0) return;
        double pct = cfgd("overkill_pct", 0.30 + level * 0.20);
        double aoeDmg = overkill * pct;
        double radius = cfgd("radius", 5.0 + level * 2.0);

        for (LivingEntity e : MathUtil.getNearbyLiving(killed.getLocation(), radius)) {
            if (e.equals(player) || e.equals(killed)) continue;
            e.damage(aoeDmg, player);
        }
        ParticleUtil.burst(killed.getLocation(), Particle.SCULK_SOUL, 30, 2.0);
        SoundUtil.play(killed.getLocation(), Sound.ENTITY_WARDEN_SONIC_BOOM, 0.8f, 1.0f);
    }

    @Override public String getDescription(int level) {
        return "§7On kill: §c" + (int)(30 + level * 20) + "% §7overkill damage dealt as AoE.";
    }
}
