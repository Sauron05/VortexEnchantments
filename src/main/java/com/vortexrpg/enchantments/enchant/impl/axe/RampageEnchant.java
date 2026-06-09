package com.vortexrpg.enchantments.enchant.impl.axe;

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

/**
 * Rampage: Each consecutive hit within 3s deals +10/15/20% more damage (stacks up to 5x).
 * Missing a hit or waiting too long resets the counter.
 */
public class RampageEnchant extends VortexEnchant {

    private static final String HIT_COUNT_KEY = "rampage_hits";
    private static final String LAST_HIT_KEY = "rampage_last_hit";

    public RampageEnchant() {
        super("rampage", "Rampage", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.AXE));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        long now = System.currentTimeMillis();
        long lastHit = plugin.getPlayerDataManager().getInt(attacker.getUniqueId(), LAST_HIT_KEY);
        int hits = plugin.getPlayerDataManager().getInt(attacker.getUniqueId(), HIT_COUNT_KEY);

        long timeout = cfgi("timeout_ms", 3000);
        int maxStacks = cfgi("max_stacks", 5);

        if (now - lastHit > timeout) {
            hits = 0;
        }

        hits = Math.min(hits + 1, maxStacks);
        plugin.getPlayerDataManager().setInt(attacker.getUniqueId(), HIT_COUNT_KEY, hits);
        plugin.getPlayerDataManager().setInt(attacker.getUniqueId(), LAST_HIT_KEY, (int) (now & 0x7FFFFFFF));

        if (hits > 1) {
            double bonusPerStack = cfgd("bonus_per_stack", 0.05 + level * 0.05);
            double multiplier = 1 + (hits - 1) * bonusPerStack;
            event.setDamage(event.getDamage() * multiplier);

            ParticleUtil.spawn(attacker.getLocation().add(0, 1.5, 0), Particle.ENCHANTED_HIT, hits * 3, 0.4);
            if (hits == maxStacks) {
                SoundUtil.play(attacker.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 0.5f, 1.5f);
                attacker.sendMessage("§4[Rampage] §cMAX STACKS! (" + maxStacks + "x)");
            }
        }
    }

    @Override
    public String getDescription(int level) {
        int pct = (int) ((0.05 + level * 0.05) * 100);
        return "§7Consecutive hits deal §c+" + pct + "% §7more damage per stack (max 5x).";
    }
}
