package com.vortexrpg.enchantments.enchant.impl.chestplate;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * Radiance: When hit below X% HP, emit a heal AoE pulse for nearby allies.
 */
public class RadianceEnchant extends VortexEnchant {
    public RadianceEnchant() { super("radiance", "Radiance", EnchantRarity.RARE, 3, List.of(ItemTarget.CHESTPLATE)); }

    @Override
    public void onDamaged(EntityDamageByEntityEvent event, Player victim, Entity attacker, int level) {
        if (!isEnabled()) return;
        if (isOnCooldown(victim)) return;
        double maxHp = victim.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue();
        double threshold = cfgd("hp_threshold", 0.40);
        if (victim.getHealth() / maxHp > threshold) return;

        double radius = cfgd("radius", 5.0);
        double healAmount = cfgd("heal_amount", 2.0 * level);

        for (Player p : victim.getWorld().getPlayers()) {
            if (p.getLocation().distanceSquared(victim.getLocation()) <= radius * radius) {
                double pMax = p.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue();
                p.setHealth(Math.min(pMax, p.getHealth() + healAmount));
            }
        }
        ParticleUtil.burst(victim.getLocation(), Particle.END_ROD, 25, 2.0);
        SoundUtil.play(victim.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 0.8f, 1.2f);
        setCooldownFromConfig(victim, "cooldown", 20.0);
    }

    @Override public String getDescription(int level) {
        return "§7Below 40% HP: heal nearby allies for §a" + (2 * level) + " §7HP. §820s CD.";
    }
}
