package com.vortexrpg.enchantments.enchant.impl.helmet;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * Aegis Crown: While below 50% HP, gain Resistance I and reflect X% of damage back. 
 */
public class AegisCrownEnchant extends VortexEnchant {
    public AegisCrownEnchant() { super("aegis_crown", "Aegis Crown", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.HELMET)); }

    @Override
    public void onDamaged(EntityDamageByEntityEvent event, Player victim, Entity attacker, int level) {
        if (!isEnabled()) return;
        double maxHp = victim.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue();
        if (victim.getHealth() / maxHp > 0.5) return;

        double reflectPct = cfgd("reflect_pct", 0.10 + level * 0.05);
        double reflected = event.getDamage() * reflectPct;

        if (attacker instanceof LivingEntity living) {
            living.damage(reflected, victim);
            ParticleUtil.spawn(living.getLocation().add(0, 1, 0), Particle.ENCHANTED_HIT, 8, 0.3);
        }
        victim.addPotionEffect(new org.bukkit.potion.PotionEffect(
                org.bukkit.potion.PotionEffectType.RESISTANCE, 40, 0, true, false, false));
    }

    @Override public String getDescription(int level) {
        int pct = (int) ((0.10 + level * 0.05) * 100);
        return "§7Below 50% HP: gain §bResistance I§7 + reflect §a" + pct + "%§7 damage.";
    }
}
