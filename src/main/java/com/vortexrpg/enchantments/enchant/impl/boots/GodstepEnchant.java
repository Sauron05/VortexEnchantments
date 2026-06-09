package com.vortexrpg.enchantments.enchant.impl.boots;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * Godstep: On lethal damage, survive, full heal, explode, time slow enemies.
 */
public class GodstepEnchant extends VortexEnchant {
    public GodstepEnchant() { super("godstep", "Godstep", EnchantRarity.MYTHIC, 3, List.of(ItemTarget.BOOTS)); }

    @Override
    public void onDamageTaken(EntityDamageEvent event, Player player, int level) {
        if (!isEnabled()) return;
        double hpAfter = player.getHealth() - event.getFinalDamage();
        if (hpAfter >= 0) return;
        if (isOnCooldown(player)) return;

        event.setDamage(0);
        double maxHp = player.getAttribute(Attribute.MAX_HEALTH).getValue();
        player.setHealth(maxHp * cfgd("heal_pct", 0.50));

        double radius = cfgd("radius", 8.0);
        double dmg = cfgd("burst_damage", 5.0 * level);
        for (LivingEntity e : MathUtil.getNearbyLiving(player.getLocation(), radius)) {
            if (e.equals(player)) continue;
            e.damage(dmg, player);
            e.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 60 + level * 20, 2, true, false, true));
        }

        int dur = cfgi("buff_duration", 60 + level * 20);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, dur, level, true, false, true));
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, dur, level - 1, true, false, true));

        ParticleUtil.burst(player.getLocation().add(0, 1, 0), Particle.TOTEM_OF_UNDYING, 50, radius);
        SoundUtil.play(player.getLocation(), Sound.ITEM_TOTEM_USE, 1.0f, 0.5f);
        setCooldownFromConfig(player, "cooldown", 300.0);
    }

    @Override public String getDescription(int level) {
        return "§7Avoid death: §6full heal§7, AOE burst, time-slow enemies. §8300s CD.";
    }
}
