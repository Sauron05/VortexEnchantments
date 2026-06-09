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
 * Temporal Echo: After hitting, the attack is repeated as phantom damage
 * after 1 second, dealing 30/40/50% of the original damage.
 */
public class TemporalEchoEnchant extends VortexEnchant {

    public TemporalEchoEnchant() {
        super("temporal_echo", "Temporal Echo", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.SWORD));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        double ratio = cfgd("echo_ratio", 0.2 + level * 0.1);
        int delayTicks = cfgi("delay_ticks", 20);
        double echoDamage = event.getDamage() * ratio;

        if (echoDamage < 0.5) return;

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (!victim.isValid() || victim.isDead()) return;
            victim.damage(echoDamage, attacker);
            ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.REVERSE_PORTAL, 15, 0.4);
            if (victim instanceof Player p) {
                p.sendMessage("§5[Echo] §7Temporal damage echoes through you!");
            }
        }, delayTicks);
    }

    @Override
    public String getDescription(int level) {
        int pct = (int) ((0.2 + level * 0.1) * 100);
        return "§7Attack echoes after 1s, dealing §c" + pct + "%§7 phantom damage.";
    }
}
