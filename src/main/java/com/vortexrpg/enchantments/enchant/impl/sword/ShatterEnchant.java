package com.vortexrpg.enchantments.enchant.impl.sword;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * Shatter: Execute mechanic - if target is below 30/40/50% health,
 * deal 2x damage and apply mining fatigue + extreme knockback.
 */
public class ShatterEnchant extends VortexEnchant {

    public ShatterEnchant() {
        super("shatter", "Shatter", EnchantRarity.EPIC, 3, List.of(ItemTarget.SWORD));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        double threshold = cfgd("health_threshold", 0.2 + level * 0.1);
        double damageMultiplier = cfgd("damage_multiplier", 2.0);
        double knockback = cfgd("knockback_strength", 1.5);

        double maxHealth = victim.getAttribute(Attribute.MAX_HEALTH).getValue();
        double healthRatio = victim.getHealth() / maxHealth;

        if (healthRatio > threshold) return;

        event.setDamage(event.getDamage() * damageMultiplier);

        victim.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 60, 2, false, false));

        org.bukkit.util.Vector kb = attacker.getLocation().getDirection().normalize().multiply(knockback);
        kb.setY(0.5);
        victim.setVelocity(kb);

        ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.CRIT, 20, 0.5);
        ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.BLOCK, 15, 0.4,
            org.bukkit.Material.STONE.createBlockData());
        SoundUtil.play(victim.getLocation(), Sound.ENTITY_IRON_GOLEM_DEATH, 0.8f, 1.5f);

        attacker.sendMessage("§c[Shatter] §7Target shattered! §c2x damage!");
    }

    @Override
    public String getDescription(int level) {
        int threshold = (int) ((0.2 + level * 0.1) * 100);
        return "§7If target is below §c" + threshold + "% HP§7: deal §c2x damage§7 + knockback.";
    }
}
