package com.vortexrpg.enchantments.enchant.impl.chestplate;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Replenish: Passive regeneration if no damage taken for 5 seconds.
 */
public class ReplenishEnchant extends VortexEnchant {
    private static final Map<UUID, Long> LAST_HIT = new HashMap<>();

    public ReplenishEnchant() { super("replenish", "Replenish", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.CHESTPLATE)); }

    @Override
    public void onDamageTaken(org.bukkit.event.entity.EntityDamageEvent event, Player player, int level) {
        if (!isEnabled()) return;
        LAST_HIT.put(player.getUniqueId(), System.currentTimeMillis());
    }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        long delay = cfgi("no_damage_ms", 5000);
        Long last = LAST_HIT.get(player.getUniqueId());
        if (last != null && System.currentTimeMillis() - last < delay) return;
        double maxHp = player.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue();
        if (player.getHealth() >= maxHp) return;
        double heal = cfgd("heal_per_tick", 0.5 * level);
        player.setHealth(Math.min(maxHp, player.getHealth() + heal));
        ParticleUtil.spawn(player.getLocation().add(0, 1, 0), Particle.HEART, 1, 0.3);
    }

    @Override public String getDescription(int level) {
        return "§7Regenerate §a" + String.format("%.1f", 0.5 * level) + " §7HP/s after 5s without damage.";
    }
}
