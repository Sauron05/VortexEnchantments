package com.vortexrpg.enchantments.enchant.impl.chestplate;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Phase Shift: When hit at low HP, teleport behind attacker. Cooldown.
 */
public class PhaseShiftEnchant extends VortexEnchant {
    public PhaseShiftEnchant() { super("phase_shift", "Phase Shift", EnchantRarity.EPIC, 3, List.of(ItemTarget.CHESTPLATE)); }

    @Override
    public void onDamaged(EntityDamageByEntityEvent event, Player victim, Entity attacker, int level) {
        if (!isEnabled()) return;
        if (!(attacker instanceof LivingEntity)) return;
        if (isOnCooldown(victim)) return;
        double maxHp = victim.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue();
        double threshold = cfgd("hp_threshold", 0.35);
        if (victim.getHealth() / maxHp > threshold) return;

        Location loc = attacker.getLocation();
        Vector behind = loc.getDirection().multiply(-1.5);
        Location dest = loc.add(behind);
        dest.setY(loc.getY());
        victim.teleport(dest);
        victim.setVelocity(loc.getDirection().normalize().multiply(0.3));

        ParticleUtil.spawn(victim.getLocation(), Particle.REVERSE_PORTAL, 20, 0.5);
        SoundUtil.play(victim.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 0.7f, 1.2f);
        setCooldownFromConfig(victim, "cooldown", 15.0);
    }

    @Override public String getDescription(int level) {
        return "§7Below 35% HP: §5teleport §7behind attacker. §815s CD.";
    }
}
