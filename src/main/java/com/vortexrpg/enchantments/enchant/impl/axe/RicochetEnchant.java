package com.vortexrpg.enchantments.enchant.impl.axe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.List;

/**
 * Ricochet: On kill, deal 30%/40%/50% of kill damage to nearest enemy within 4/5/6 blocks.
 */
@SuppressWarnings("deprecation")
public class RicochetEnchant extends VortexEnchant {

    private static final double[] DAMAGE_PCT = {0.30, 0.40, 0.50};
    private static final double[] RADIUS = {4.0, 5.0, 6.0};

    public RicochetEnchant() {
        super("ricochet", "Ricochet", EnchantRarity.EPIC, 3, List.of(ItemTarget.AXE));
    }

    @Override
    public void onKill(EntityDeathEvent event, Player killer, LivingEntity killed, int level) {
        if (!isEnabled()) return;
        double radius = cfg("bounce_radius", RADIUS[level - 1]);
        double damagePct = cfg("damage_percent", DAMAGE_PCT[level - 1]);
        double killDamage = killed.getMaxHealth();  // Approximate; actual kill event doesn't expose last hit damage

        LivingEntity nearest = MathUtil.getNearestLiving(killed.getLocation(), radius, e -> !e.equals(killer) && !e.equals(killed));
        if (nearest != null) {
            nearest.damage(killDamage * damagePct, killer);
        }
    }

    @Override
    public String getDescription() { return "Kills ricochet damage to the nearest enemy."; }

    @Override
    public String getDescription(int level) {
        return "§7On kill: §c" + (int)(DAMAGE_PCT[level-1]*100) + "%§7 damage ricochets to nearest enemy within §e" + RADIUS[level-1] + "§7 blocks.";
    }
}
