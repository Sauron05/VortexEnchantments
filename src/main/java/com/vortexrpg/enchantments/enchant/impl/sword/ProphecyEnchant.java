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
import java.util.concurrent.ConcurrentHashMap;

/**
 * Prophecy: Hit the same target 3 times to trigger a prophecy.
 * Target gains Glowing effect for 5/8/10 seconds, visible through blocks.
 */
public class ProphecyEnchant extends VortexEnchant {

    private final ConcurrentHashMap<String, Integer> hitCounts = new ConcurrentHashMap<>();

    public ProphecyEnchant() {
        super("prophecy", "Prophecy", EnchantRarity.RARE, 3, List.of(ItemTarget.SWORD));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        int requiredHits = cfgi("required_hits", 3);
        int glowTicks = cfgi("glow_ticks", 100) + (level - 1) * 60;

        String key = attacker.getUniqueId() + ":" + victim.getUniqueId();
        int hits = hitCounts.getOrDefault(key, 0) + 1;

        if (hits >= requiredHits) {
            hitCounts.remove(key);
            victim.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, glowTicks, 0, false, false));
            ParticleUtil.spawn(victim.getLocation().add(0, 2, 0), Particle.END_ROD, 20, 0.5);
            SoundUtil.play(victim.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 0.8f, 2.0f);

            attacker.sendMessage("§e[Prophecy] §7Target revealed! Visible through blocks.");
            if (victim instanceof Player p) {
                p.sendMessage("§e[Prophecy] §7You have been marked! You are §eglowing§7.");
            }
        } else {
            hitCounts.put(key, hits);
        }
    }

    @Override
    public String getDescription(int level) {
        int secs = (100 + (level - 1) * 60) / 20;
        return "§7Hit the same target §e3x§7: they §eglow§7 through blocks for §e" + secs + "s§7.";
    }
}
