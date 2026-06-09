package com.vortexrpg.enchantments.enchant.impl.sword;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * Tesseract: On hit, damage replicates to all entities within a 3/4/5 block cube
 * around the target, dealing 40/50/60% of the original damage.
 */
public class TesseractEnchant extends VortexEnchant {

    public TesseractEnchant() {
        super("tesseract", "Tesseract", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.SWORD));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        double radius = cfgd("radius", 3.0) + (level - 1);
        double damageRatio = cfgd("damage_ratio", 0.3 + level * 0.1);

        double replicatedDamage = event.getDamage() * damageRatio;
        if (replicatedDamage < 0.5) return;

        ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.END_ROD, 15, radius * 0.4);
        SoundUtil.play(victim.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 0.6f, 2.0f);

        for (Entity e : victim.getNearbyEntities(radius, radius, radius)) {
            if (e.equals(attacker) || !(e instanceof LivingEntity le)) continue;
            le.damage(replicatedDamage, attacker);
            ParticleUtil.spawn(le.getLocation().add(0, 1, 0), Particle.END_ROD, 5, 0.3);
        }
    }

    @Override
    public String getDescription(int level) {
        int radius = 3 + (level - 1);
        int pct = (int) ((0.3 + level * 0.1) * 100);
        return "§7Replicates §c" + pct + "% §7damage to all enemies within §e" + radius + " blocks§7.";
    }
}
