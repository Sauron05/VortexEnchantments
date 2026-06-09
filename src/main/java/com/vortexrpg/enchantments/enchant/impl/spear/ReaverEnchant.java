package com.vortexrpg.enchantments.enchant.impl.spear;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.List;

/**
 * Reaver: Each kill within 5 seconds stacks +15/20/25% damage.
 * Max 5 stacks, lasts 8 seconds from last kill.
 */
public class ReaverEnchant extends VortexEnchant {

    private static final String STACKS_KEY = "reaver_stacks";
    private static final String TIME_KEY = "reaver_time";

    public ReaverEnchant() {
        super("reaver", "Reaver", EnchantRarity.EPIC, 3, List.of(ItemTarget.SPEAR));
    }

    @Override
    public void onKill(EntityDeathEvent event, Player killer, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        var pdm = plugin.getPlayerDataManager();
        long now = System.currentTimeMillis();
        long lastKill = pdm.getLong(killer.getUniqueId(), TIME_KEY);

        int stacks;
        if (now - lastKill < 5000) {
            stacks = Math.min(pdm.getInt(killer.getUniqueId(), STACKS_KEY) + 1, 5);
        } else {
            stacks = 1;
        }

        pdm.setInt(killer.getUniqueId(), STACKS_KEY, stacks);
        pdm.setLong(killer.getUniqueId(), TIME_KEY, now);

        ParticleUtil.spawn(killer.getLocation().add(0, 1, 0), Particle.SOUL_FIRE_FLAME, 8 + stacks * 3, 0.4);
        SoundUtil.play(killer.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.6f, 0.8f + stacks * 0.1f);
    }

    @Override
    public void onAttack(org.bukkit.event.entity.EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        var pdm = plugin.getPlayerDataManager();
        long lastKill = pdm.getLong(attacker.getUniqueId(), TIME_KEY);
        if (System.currentTimeMillis() - lastKill > 8000) {
            pdm.setInt(attacker.getUniqueId(), STACKS_KEY, 0);
            return;
        }

        int stacks = pdm.getInt(attacker.getUniqueId(), STACKS_KEY);
        if (stacks > 0) {
            double bonusPer = cfgd("bonus_per_stack", 0.10 + level * 0.05);
            event.setDamage(event.getDamage() * (1.0 + stacks * bonusPer));
        }
    }

    @Override
    public String getDescription(int level) {
        int pct = (int) ((0.10 + level * 0.05) * 100);
        return "§7Rapid kills: §c+" + pct + "% §7damage/stack (max §e5§7, 8s window).";
    }
}
