package com.vortexrpg.enchantments.enchant.impl.helmet;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.List;

/**
 * Mirage: After being hit 3 times quickly, become briefly invisible (1-3s). 12s CD.
 */
public class MirageEnchant extends VortexEnchant {
    private static final Map<UUID, int[]> HIT_DATA = new HashMap<>();

    public MirageEnchant() { super("mirage", "Mirage", EnchantRarity.EPIC, 3, List.of(ItemTarget.HELMET)); }

    @Override
    public void onDamaged(EntityDamageByEntityEvent event, Player victim, Entity attacker, int level) {
        if (!isEnabled()) return;
        if (isOnCooldown(victim)) return;

        UUID id = victim.getUniqueId();
        int[] data = HIT_DATA.computeIfAbsent(id, k -> new int[]{0, (int)(System.currentTimeMillis() / 1000)});
        long now = System.currentTimeMillis() / 1000;
        if (now - data[1] > 4) { data[0] = 0; }
        data[0]++;
        data[1] = (int) now;

        int threshold = cfgi("hit_threshold", 3);
        if (data[0] >= threshold) {
            data[0] = 0;
            int dur = cfgi("duration", 20 + level * 20);
            victim.addPotionEffect(new org.bukkit.potion.PotionEffect(
                    org.bukkit.potion.PotionEffectType.INVISIBILITY, dur, 0, true, false, false));
            ParticleUtil.spawn(victim.getLocation(), Particle.POOF, 15, 0.5);
            setCooldownFromConfig(victim, "cooldown", 12.0);
        }
    }

    @Override public String getDescription(int level) {
        return "§7After 3 quick hits: become §binvisible §7for " + (1 + level) + "s. §812s CD.";
    }
}
