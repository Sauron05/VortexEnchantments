package com.vortexrpg.enchantments.enchant.impl.crossbow;

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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.List;

/**
 * Resonant: Consecutive hits on any target increase damage by 10/15/20%.
 * Stacks reset after 3s without hitting.
 */
public class ResonantEnchant extends VortexEnchant {

    private static final Map<UUID, Integer> STACKS = new HashMap<>();
    private static final Map<UUID, Long> LAST_HIT = new HashMap<>();

    public ResonantEnchant() {
        super("resonant", "Resonant", EnchantRarity.RARE, 3, List.of(ItemTarget.CROSSBOW));
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        UUID sid = shooter.getUniqueId();
        long now = System.currentTimeMillis();
        long window = cfgi("window_ms", 3000);

        Long last = LAST_HIT.get(sid);
        if (last == null || now - last > window) {
            STACKS.put(sid, 1);
        } else {
            STACKS.merge(sid, 1, (a, b) -> a + b);
        }
        LAST_HIT.put(sid, now);

        int stacks = Math.min(STACKS.getOrDefault(sid, 1), cfgi("max_stacks", 5));
        double bonusPerStack = cfgd("bonus_per_stack", 0.05 + level * 0.05);
        double totalBonus = stacks * bonusPerStack;

        event.setDamage(event.getDamage() * (1.0 + totalBonus));

        if (stacks >= 3) {
            ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.ENCHANTED_HIT, stacks * 3, 0.3);
            SoundUtil.play(victim.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 0.5f, 0.8f + stacks * 0.15f);
        }
    }

    @Override
    public String getDescription(int level) {
        int pct = (int) ((0.05 + level * 0.05) * 100);
        return "§7Consecutive hits: §e+" + pct + "% §7per stack (max 5, 3s window).";
    }
}
