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
 * Juggernaut: While attacking, gain temporary damage resistance.
 * Each hit grants Resistance I for 2/3/4s. While active, movement slowed slightly.
 */
public class JuggernautEnchant extends VortexEnchant {

    public JuggernautEnchant() {
        super("juggernaut", "Juggernaut", EnchantRarity.EPIC, 3, List.of(ItemTarget.AXE));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        int durationTicks = cfgi("duration_ticks", 20 + level * 20);
        int resistLevel = cfgi("resistance_level", 0);

        attacker.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, durationTicks, resistLevel, false, false));
        attacker.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, durationTicks, 0, false, false));

        ParticleUtil.spawn(attacker.getLocation().add(0, 1, 0), Particle.ENCHANTED_HIT, 5, 0.3);
        SoundUtil.play(attacker.getLocation(), Sound.ITEM_ARMOR_EQUIP_IRON, 0.4f, 0.8f);
    }

    @Override
    public String getDescription(int level) {
        double secs = (20 + level * 20) / 20.0;
        return "§7Attacks grant §bResistance I §7for §e" + secs + "s§7 (slightly slowed).";
    }
}
