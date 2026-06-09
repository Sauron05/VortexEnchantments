package com.vortexrpg.enchantments.enchant.impl.sword;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * Paradox Blade: 30/40/50% of damage dealt is stored and re-applied
 * 3 seconds later as unavoidable true damage.
 */
public class ParadoxBladeEnchant extends VortexEnchant {

    public ParadoxBladeEnchant() {
        super("paradox_blade", "Paradox Blade", EnchantRarity.EPIC, 3, List.of(ItemTarget.SWORD));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        double storeRatio = cfgd("store_ratio", 0.2 + level * 0.1);
        int delayTicks = cfgi("delay_ticks", 60);
        double storedDamage = event.getDamage() * storeRatio;

        if (storedDamage < 0.5) return;

        ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.ENCHANT, 8, 0.3);

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (!victim.isValid() || victim.isDead()) return;
            double health = victim.getHealth();
            victim.setHealth(Math.max(0.0, health - storedDamage));
            ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.REVERSE_PORTAL, 20, 0.5);
            if (victim instanceof Player p) {
                p.sendMessage("§5[Paradox] §7Past damage catches up! §c-" + String.format("%.1f", storedDamage) + " HP");
            }
        }, delayTicks);
    }

    @Override
    public String getDescription(int level) {
        int pct = (int) ((0.2 + level * 0.1) * 100);
        return "§7Stores §c" + pct + "%§7 damage, re-applied as §4true damage§7 after 3s.";
    }
}
