package com.vortexrpg.enchantments.enchant.impl.leggings;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * RetaliationField: On lethal damage avoided (absorption), nearby enemies get Slowness + Weakness.
 */
public class RetaliationFieldEnchant extends VortexEnchant {
    public RetaliationFieldEnchant() { super("retaliation_field", "Retaliation Field", EnchantRarity.EPIC, 3, List.of(ItemTarget.LEGGINGS)); }

    @Override
    public void onDamageTaken(EntityDamageEvent event, Player player, int level) {
        if (!isEnabled()) return;
        double hpAfter = player.getHealth() - event.getFinalDamage();
        if (hpAfter > 4.0) return;
        if (hpAfter <= 0) return;
        if (isOnCooldown(player)) return;

        double radius = cfgd("radius", 5.0 + level);
        int dur = cfgi("debuff_duration", 40 + level * 20);
        for (LivingEntity e : MathUtil.getNearbyLiving(player.getLocation(), radius)) {
            if (e.equals(player)) continue;
            e.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, dur, level - 1, true, false, true));
            e.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, dur, level - 1, true, false, true));
        }
        ParticleUtil.burst(player.getLocation().add(0, 1, 0), Particle.ANGRY_VILLAGER, 15, radius);
        SoundUtil.play(player.getLocation(), Sound.ENTITY_WARDEN_ROAR, 0.4f, 1.5f);
        setCooldownFromConfig(player, "cooldown", 20.0);
    }

    @Override public String getDescription(int level) {
        return "§7Near-death: slow + weaken nearby enemies for " + (2 + level) + "s. §820s CD.";
    }
}
