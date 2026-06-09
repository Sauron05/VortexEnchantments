package com.vortexrpg.enchantments.enchant.impl.chestplate;

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
 * Regenerate Plate: On kill, rapidly regenerate to full HP over X seconds.
 */
public class RegeneratePlateEnchant extends VortexEnchant {
    public RegeneratePlateEnchant() { super("regenerate_plate", "Regenerate Plate", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.CHESTPLATE)); }

    @Override
    public void onKill(EntityDamageByEntityEvent event, Player player, LivingEntity killed, int level) {
        if (!isEnabled()) return;
        double maxHp = player.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue();
        double totalHeal = maxHp - player.getHealth();
        if (totalHeal <= 0) return;
        int ticks = cfgi("regen_ticks", 200);
        double perTick = totalHeal / (ticks / 20.0);

        for (int i = 0; i < ticks; i += 20) {
            final double heal = perTick;
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                if (!player.isOnline() || player.isDead()) return;
                double max = player.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue();
                player.setHealth(Math.min(max, player.getHealth() + heal));
                ParticleUtil.spawn(player.getLocation().add(0, 1, 0), Particle.HEART, 2, 0.3);
            }, i);
        }
    }

    @Override public String getDescription(int level) {
        return "§7On kill: regenerate to §afull HP §7over 10s.";
    }
}
