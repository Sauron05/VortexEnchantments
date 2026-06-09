package com.vortexrpg.enchantments.enchant.impl.spear;

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
 * Petrify: 8/12/16% chance to fully immobilize the target —
 * Mining Fatigue X + Slowness X + Darkness — for 1.5/2/2.5 seconds.
 */
public class PetrifyEnchant extends VortexEnchant {

    public PetrifyEnchant() {
        super("petrify", "Petrify", EnchantRarity.EPIC, 3, List.of(ItemTarget.SPEAR));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        double chance = cfgd("chance", 0.04 + level * 0.04);
        if (Math.random() > chance) return;

        int durationTicks = (int) (cfgd("duration", 1.0 + level * 0.5) * 20);

        victim.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, durationTicks, 9, false, false));
        victim.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, durationTicks, 9, false, false));
        victim.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, durationTicks, 0, false, false));

        ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.ASH, 30, 0.5);
        ParticleUtil.drawCircle(victim.getLocation(), 1.0, 12, Particle.ENCHANTED_HIT);
        SoundUtil.play(victim.getLocation(), Sound.BLOCK_DEEPSLATE_BREAK, 1.0f, 0.3f);

        if (victim instanceof Player p) {
            p.sendMessage("§8[Petrify] §7You have been turned to stone!");
        }
    }

    @Override
    public String getDescription(int level) {
        int pct = (int) ((0.04 + level * 0.04) * 100);
        String dur = String.format("%.1f", 1.0 + level * 0.5);
        return "§7" + pct + "% chance to §8petrify §7target for §e" + dur + "s§7.";
    }
}
