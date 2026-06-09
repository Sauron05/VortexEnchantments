package com.vortexrpg.enchantments.enchant.impl.leggings;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import java.util.List;

/**
 * WardenLegs: Accumulate damage taken, converting a % of it into a heal burst after X seconds.
 */
public class WardenLegsEnchant extends VortexEnchant {
    private static final Map<UUID, double[]> ACCUM = new HashMap<>();

    public WardenLegsEnchant() { super("warden_legs", "Warden Legs", EnchantRarity.EPIC, 3, List.of(ItemTarget.LEGGINGS)); }

    @Override
    public void onDamageTaken(EntityDamageEvent event, Player player, int level) {
        if (!isEnabled()) return;
        UUID uid = player.getUniqueId();
        double[] data = ACCUM.computeIfAbsent(uid, k -> new double[]{0, System.currentTimeMillis()});
        data[0] += event.getFinalDamage();
    }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        UUID uid = player.getUniqueId();
        double[] data = ACCUM.get(uid);
        if (data == null || data[0] <= 0) return;
        long elapsed = System.currentTimeMillis() - (long) data[1];
        double window = cfgd("window_seconds", 5.0) * 1000;
        if (elapsed < window) return;
        double pct = cfgd("heal_pct", 0.10 * level);
        double heal = data[0] * pct;
        double maxHp = player.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue();
        player.setHealth(Math.min(maxHp, player.getHealth() + heal));
        data[0] = 0;
        data[1] = System.currentTimeMillis();
    }

    @Override public String getDescription(int level) {
        return "§7Accumulate damage, then heal §a" + (10 * level) + "% §7after 5s.";
    }
}
