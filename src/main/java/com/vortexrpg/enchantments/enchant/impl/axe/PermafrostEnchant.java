package com.vortexrpg.enchantments.enchant.impl.axe;

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
 * Permafrost: Attacks apply a freezing debuff. Slowness II + no sprint + frost particles.
 * Duration: 3/4/5 seconds. Frozen targets take 20% more axe damage.
 */
public class PermafrostEnchant extends VortexEnchant {

    public PermafrostEnchant() {
        super("permafrost", "Permafrost", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.AXE));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        int durationTicks = cfgi("freeze_ticks", 40 + level * 20);

        if (victim.hasPotionEffect(PotionEffectType.SLOWNESS)) {
            double frozenBonus = cfgd("frozen_bonus", 0.2);
            event.setDamage(event.getDamage() * (1 + frozenBonus));
        }

        victim.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, durationTicks, 1, false, false));
        victim.setFreezeTicks(Math.max(victim.getFreezeTicks(), durationTicks));

        ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.SNOWFLAKE, 15, 0.5);
        SoundUtil.play(victim.getLocation(), Sound.BLOCK_GLASS_BREAK, 0.6f, 1.5f);
    }

    @Override
    public String getDescription(int level) {
        double secs = (40 + level * 20) / 20.0;
        return "§7Freezes target for §b" + secs + "s§7. Frozen targets take §c+20% §7damage.";
    }
}
