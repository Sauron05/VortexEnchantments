package com.vortexrpg.enchantments.enchant.impl.hammer;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * Daze: Hammer blows inflict Nausea for 3/5/7 seconds.
 */
public class DazeEnchant extends VortexEnchant {

    public DazeEnchant() {
        super("daze", "Daze", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.HAMMER));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        int duration = cfgi("duration", (1 + level * 2)) * 20;
        victim.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, duration, 0, false, true));
        ParticleUtil.spawn(victim.getLocation().add(0, 2, 0), Particle.ENCHANTED_HIT, 8, 0.4);
    }

    @Override
    public String getDescription(int level) {
        int dur = 1 + level * 2;
        return "§7Inflicts §eNausea §7for §e" + dur + "s §7on hit.";
    }
}
