package com.vortexrpg.enchantments.enchant.impl.helmet;

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
 * Dreamweaver: On low-HP hit, teleport randomly within 8 blocks behind the attacker. 15s CD.
 */
public class DreamweaverEnchant extends VortexEnchant {
    public DreamweaverEnchant() { super("dreamweaver", "Dreamweaver", EnchantRarity.EPIC, 3, List.of(ItemTarget.HELMET)); }

    @Override
    public void onDamaged(EntityDamageByEntityEvent event, Player victim, Entity attacker, int level) {
        if (!isEnabled()) return;
        if (isOnCooldown(victim)) return;
        double threshold = cfgd("hp_threshold", 0.3 + level * 0.05);
        double maxHp = victim.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue();
        if (victim.getHealth() / maxHp > threshold) return;

        org.bukkit.util.Vector behind = attacker.getLocation().getDirection().normalize().multiply(-3);
        org.bukkit.Location dest = attacker.getLocation().add(behind);
        dest.setY(attacker.getLocation().getY());
        if (dest.getBlock().isPassable() && dest.clone().add(0, 1, 0).getBlock().isPassable()) {
            ParticleUtil.spawn(victim.getLocation(), Particle.PORTAL, 30, 0.5);
            victim.teleport(dest);
            ParticleUtil.spawn(dest, Particle.PORTAL, 30, 0.5);
            SoundUtil.play(dest, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.5f);
            setCooldownFromConfig(victim, "cooldown", 15.0);
        }
    }

    @Override public String getDescription(int level) {
        int pct = (int) ((0.3 + level * 0.05) * 100);
        return "§7Below §c" + pct + "% §7HP: teleport behind attacker. §815s CD.";
    }
}
