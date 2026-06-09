package com.vortexrpg.enchantments.enchant.impl.hammer;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * Bedrock: If player stood still for 2+ seconds, gain 15/25/35% damage reduction.
 * Immovable-object defense enchant using onDamaged.
 */
public class BedrockEnchant extends VortexEnchant {

    private static final java.util.Map<java.util.UUID, org.bukkit.Location> LAST_LOC = new java.util.concurrent.ConcurrentHashMap<>();
    private static final java.util.Map<java.util.UUID, Long> STAYED_SINCE = new java.util.concurrent.ConcurrentHashMap<>();

    public BedrockEnchant() {
        super("bedrock", "Bedrock", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.HAMMER));
    }

    @Override
    public void onDamaged(EntityDamageByEntityEvent event, Player victim, Entity attacker, int level) {
        if (!isEnabled()) return;

        java.util.UUID id = victim.getUniqueId();
        org.bukkit.Location current = victim.getLocation();
        org.bukkit.Location last = LAST_LOC.get(id);

        if (last != null && last.getWorld().equals(current.getWorld())
                && last.distanceSquared(current) < 0.25) {
            // Hasn't moved more than 0.5 blocks
            long since = STAYED_SINCE.getOrDefault(id, System.currentTimeMillis());
            if (System.currentTimeMillis() - since >= 2000) {
                double reduction = cfgd("reduction", 0.05 + level * 0.10);
                event.setDamage(event.getDamage() * (1.0 - reduction));
                ParticleUtil.spawn(victim.getLocation(), Particle.ENCHANTED_HIT, 8, 0.3);
            }
        } else {
            STAYED_SINCE.put(id, System.currentTimeMillis());
        }
        LAST_LOC.put(id, current);
    }

    @Override
    public String getDescription(int level) {
        int pct = (int) ((0.05 + level * 0.10) * 100);
        return "§7Stand still 2s: §a-" + pct + "% §7incoming damage.";
    }
}
