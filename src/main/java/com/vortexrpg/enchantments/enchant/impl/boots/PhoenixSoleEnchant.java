package com.vortexrpg.enchantments.enchant.impl.boots;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.List;

/**
 * Phoenix Sole: Survive lethal damage, gain fire immunity and regen.
 */
public class PhoenixSoleEnchant extends VortexEnchant {
    public PhoenixSoleEnchant() { super("phoenix_sole", "Phoenix Sole", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.BOOTS)); }

    @Override
    public void onDamageTaken(EntityDamageEvent event, Player player, int level) {
        if (!isEnabled()) return;
        double hpAfter = player.getHealth() - event.getFinalDamage();
        if (hpAfter >= 0) return;
        if (isOnCooldown(player)) return;

        event.setDamage(0);
        double maxHp = player.getAttribute(Attribute.MAX_HEALTH).getValue();
        player.setHealth(Math.max(1.0, maxHp * cfgd("heal_pct", 0.15 * level)));
        player.setFireTicks(0);
        int dur = cfgi("regen_duration", 60 + level * 20);
        player.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.REGENERATION, dur, level - 1, true, false, true));
        player.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.FIRE_RESISTANCE, dur, 0, true, false, true));
        ParticleUtil.burst(player.getLocation().add(0, 1, 0), Particle.FLAME, 30, 2.0);
        SoundUtil.play(player.getLocation(), Sound.ITEM_TOTEM_USE, 0.8f, 1.0f);
        setCooldownFromConfig(player, "cooldown", 120.0);
    }

    @Override public String getDescription(int level) {
        return "§7Survive lethal damage: §6fire immunity §7+ regen. §8120s CD.";
    }
}
