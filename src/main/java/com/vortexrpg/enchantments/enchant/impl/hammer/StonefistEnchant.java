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
 * Stonefist: Inflicts Mining Fatigue I/II/III for 3 seconds — slows attack speed & mining.
 */
public class StonefistEnchant extends VortexEnchant {

    public StonefistEnchant() {
        super("stonefist", "Stonefist", EnchantRarity.RARE, 3, List.of(ItemTarget.HAMMER));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        int duration = cfgi("duration", 60);
        int amp = level - 1;
        victim.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, duration, amp, false, true));

        ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.DUST_PLUME, 8, 0.3);
    }

    @Override
    public String getDescription(int level) {
        return "§7Inflicts §eMining Fatigue " + level + " §7for §e3s §7on hit.";
    }
}
