package com.vortexrpg.enchantments.enchant.impl.hammer;

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

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Megaton: Each second NOT attacking charges +10/15/20% damage (max 5 stacks).
 * Patient warriors hit hardest.
 */
public class MegatonEnchant extends VortexEnchant {

    private static final Map<UUID, Long> LAST_ATTACK = new ConcurrentHashMap<>();

    public MegatonEnchant() {
        super("megaton", "Megaton", EnchantRarity.EPIC, 3, List.of(ItemTarget.HAMMER));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        UUID id = attacker.getUniqueId();
        long now = System.currentTimeMillis();
        long last = LAST_ATTACK.getOrDefault(id, now - 5000L);
        long elapsed = now - last;

        int stacks = (int) Math.min(elapsed / 1000, cfgi("max_stacks", 5));
        if (stacks > 0) {
            double perStack = cfgd("per_stack", 0.05 + level * 0.05);
            event.setDamage(event.getDamage() * (1.0 + stacks * perStack));

            ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.SONIC_BOOM, 1, 0.1);
            SoundUtil.play(victim.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.5f, 0.5f + stacks * 0.2f);
        }

        LAST_ATTACK.put(id, now);
    }

    @Override
    public String getDescription(int level) {
        int pct = (int) ((0.05 + level * 0.05) * 100);
        return "§7Wait 1s+: §c+" + pct + "% §7per second §8(max 5 stacks).";
    }
}
