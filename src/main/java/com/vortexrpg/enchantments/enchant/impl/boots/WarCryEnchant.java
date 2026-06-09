package com.vortexrpg.enchantments.enchant.impl.boots;

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
 * WarCry: On kill, gain massive buffs.
 */
public class WarCryEnchant extends VortexEnchant {
    public WarCryEnchant() { super("war_cry", "War Cry", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.BOOTS)); }

    @Override
    public void onKill(EntityDamageByEntityEvent event, Player player, LivingEntity killed, int level) {
        if (!isEnabled()) return;
        if (isOnCooldown(player)) return;
        int dur = cfgi("duration", 60 + level * 20);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, dur, level - 1, true, false, true));
        player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, dur, 0, true, false, true));
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, dur, 0, true, false, true));
        ParticleUtil.burst(player.getLocation().add(0, 1, 0), Particle.TOTEM_OF_UNDYING, 15, 1.5);
        SoundUtil.play(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 0.3f, 1.5f);
        setCooldownFromConfig(player, "cooldown", 30.0);
    }

    @Override public String getDescription(int level) {
        return "§7On kill: gain §bSpeed§7, §cStrength§7, §aRegen §7for " + (3 + level) + "s. §830s CD.";
    }
}
