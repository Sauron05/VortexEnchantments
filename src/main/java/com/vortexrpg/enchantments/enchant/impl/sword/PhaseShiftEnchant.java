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
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * Phase Shift: On kill, become temporarily invisible and take no damage
 * for 1/1.5/2 seconds. Perfect for escaping or repositioning.
 */
public class PhaseShiftEnchant extends VortexEnchant {

    public PhaseShiftEnchant() {
        super("phase_shift", "Phase Shift", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.SWORD));
    }

    @Override
    public void onKill(EntityDeathEvent event, Player killer, LivingEntity killed, int level) {
        if (!isEnabled()) return;
        if (isOnCooldown(killer)) return;

        double cooldown = cfgd("cooldown_seconds", 15.0);
        int durationTicks = cfgi("duration_ticks", 20) + (level - 1) * 10;

        setCooldownSeconds(killer, cooldown);

        killer.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, durationTicks, 0, false, false));
        killer.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, durationTicks, 4, false, false));
        killer.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, durationTicks, 1, false, false));

        ParticleUtil.spawn(killer.getLocation(), Particle.REVERSE_PORTAL, 30, 0.8);
        SoundUtil.play(killer.getLocation(), Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, 1.0f, 1.5f);
        killer.sendMessage("§5[Phase Shift] §7You phase out of reality!");

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (killer.isOnline()) {
                ParticleUtil.spawn(killer.getLocation(), Particle.REVERSE_PORTAL, 20, 0.5);
                SoundUtil.play(killer.getLocation(), Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, 0.8f, 0.8f);
            }
        }, durationTicks);
    }

    @Override
    public String getDescription(int level) {
        double secs = 1.0 + (level - 1) * 0.5;
        return "§7On kill: become §5invisible§7 and §binvulnerable§7 for §e" + secs + "s§7.";
    }
}
