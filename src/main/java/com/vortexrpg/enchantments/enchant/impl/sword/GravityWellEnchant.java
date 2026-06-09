package com.vortexrpg.enchantments.enchant.impl.sword;

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
 * Gravity Well: On hit, pin victim to the ground. They can't jump and
 * get extreme slow falling + jump debuff for 3/4/5 seconds.
 */
public class GravityWellEnchant extends VortexEnchant {

    public GravityWellEnchant() {
        super("gravity_well", "Gravity Well", EnchantRarity.EPIC, 3, List.of(ItemTarget.SWORD));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        if (isOnCooldown(attacker)) return;

        double cooldown = cfgd("cooldown_seconds", 10.0);
        int durationTicks = cfgi("duration_ticks", 60) + (level - 1) * 20;

        setCooldownSeconds(attacker, cooldown);

        victim.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, durationTicks, 2, false, false));
        victim.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, durationTicks, 128, false, false));

        victim.setVelocity(victim.getVelocity().setY(-1.0));

        ParticleUtil.drawCircle(victim.getLocation(), 1.5, 12, Particle.CRIT);
        SoundUtil.play(victim.getLocation(), Sound.ENTITY_IRON_GOLEM_ATTACK, 0.8f, 0.5f);

        if (victim instanceof Player p) {
            p.sendMessage("§5[Gravity Well] §7You're pinned to the ground!");
        }
    }

    @Override
    public String getDescription(int level) {
        int secs = 3 + (level - 1);
        return "§7Pin target to the §5ground§7 for §e" + secs + "s§7. No jumping, heavy slow.";
    }
}
