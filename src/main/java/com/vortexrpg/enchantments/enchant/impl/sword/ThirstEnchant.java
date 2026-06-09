package com.vortexrpg.enchantments.enchant.impl.sword;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.List;

/**
 * Thirst: Each kill within window adds stacking damage bonus. Resets after window with no kills.
 */
public class ThirstEnchant extends VortexEnchant {

    private static final double[] BONUS_PER_STACK = {0.10, 0.12, 0.15};

    public ThirstEnchant() {
        super("thirst", "Thirst", EnchantRarity.EPIC, 3, List.of(ItemTarget.SWORD));
    }

    @Override
    public void onKill(EntityDeathEvent event, Player killer, LivingEntity killed, int level) {
        if (!isEnabled()) return;
        long windowMs = (long) (cfg("kill_window_seconds", 10.0) * 1000);
        int maxStacks = cfgi("max_stacks", 10);

        plugin.getPlayerDataManager().recordKill(killer.getUniqueId());
        int stacks = plugin.getPlayerDataManager().getRecentKillCount(killer.getUniqueId(), windowMs);
        plugin.getPlayerDataManager().setThirstStacks(killer.getUniqueId(), Math.min(stacks, maxStacks));

        ParticleUtil.spawn(killer.getLocation().add(0, 1, 0), Particle.ENCHANTED_HIT, 6, 0.3);
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        long windowMs = (long) (cfg("kill_window_seconds", 10.0) * 1000);
        int stacks = plugin.getPlayerDataManager().getRecentKillCount(attacker.getUniqueId(), windowMs);
        if (stacks <= 0) return;

        double bonusPct = BONUS_PER_STACK[level - 1] * stacks;
        event.setDamage(event.getDamage() * (1.0 + bonusPct));
    }

    @Override
    public String getDescription() { return "Killing spree builds stacking damage bonus. Resets after 10s without a kill."; }

    @Override
    public String getDescription(int level) {
        int pct = (int) (BONUS_PER_STACK[level - 1] * 100);
        return "§a+" + pct + "% §7damage per kill within §e10s§7. Max stacks: §c10§7.";
    }
}
