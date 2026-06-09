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
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Rootgrasp: Roots the target in place for a duration.
 * Applies Slowness 127 (immobilize) + prevents jumping.
 */
public class RootgraspEnchant extends VortexEnchant {

    public RootgraspEnchant() {
        super("rootgrasp", "Rootgrasp", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.AXE));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        if (isOnCooldown(attacker)) return;

        double chance = cfgd("chance", 0.15 + level * 0.1);
        if (Math.random() > chance) return;

        int durationTicks = cfgi("root_ticks", 20 + level * 10);

        victim.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, durationTicks, 127, false, false));
        victim.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, durationTicks, 128, false, false));
        victim.setVelocity(new Vector(0, 0, 0));

        ParticleUtil.spawn(victim.getLocation(), Particle.HAPPY_VILLAGER, 20, 0.5);
        ParticleUtil.drawCircle(victim.getLocation(), 1.0, 12, Particle.HAPPY_VILLAGER);
        SoundUtil.play(victim.getLocation(), Sound.BLOCK_GRASS_BREAK, 1.0f, 0.5f);

        if (victim instanceof Player p) {
            p.sendMessage("§2[Rootgrasp] §7Roots hold you in place!");
        }

        setCooldownFromConfig(attacker, "cooldown", 6);
    }

    @Override
    public String getDescription(int level) {
        int pct = (int) ((0.15 + level * 0.1) * 100);
        double secs = (20 + level * 10) / 20.0;
        return "§7" + pct + "% chance to §2root §7target for §e" + secs + "s§7.";
    }
}
