package com.vortexrpg.enchantments.enchant.impl.hammer;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * Pummel: 10/15/20% chance to stun the target (Slowness X + Mining Fatigue X)
 * for 0.5 seconds — brief but devastating.
 */
public class PummelEnchant extends VortexEnchant {

    public PummelEnchant() {
        super("pummel", "Pummel", EnchantRarity.COMMON, 3, List.of(ItemTarget.HAMMER));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        double chance = cfgd("chance", 0.05 + level * 0.05);
        if (Math.random() > chance) return;

        int stunTicks = cfgi("stun_ticks", 10);
        victim.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, stunTicks, 9, false, false));
        victim.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, stunTicks, 9, false, false));

        ParticleUtil.spawn(victim.getLocation().add(0, 2, 0), Particle.CRIT, 10, 0.3);
        SoundUtil.play(victim.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.5f, 2.0f);
    }

    @Override
    public String getDescription(int level) {
        int pct = (int) ((0.05 + level * 0.05) * 100);
        return "§7" + pct + "% chance to §estun §7target for §e0.5s§7.";
    }
}
