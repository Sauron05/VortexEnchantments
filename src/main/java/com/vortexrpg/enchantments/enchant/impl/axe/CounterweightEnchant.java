package com.vortexrpg.enchantments.enchant.impl.axe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Counterweight: Increased knockback dealt + knockback resistance while attacking.
 * KB multiplier: 1.5/2.0/2.5x. KB resistance: 20/40/60% while attacking.
 */
public class CounterweightEnchant extends VortexEnchant {

    public CounterweightEnchant() {
        super("counterweight", "Counterweight", EnchantRarity.RARE, 3, List.of(ItemTarget.AXE));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        double kbMultiplier = cfgd("kb_multiplier", 1.0 + level * 0.5);

        Vector direction = victim.getLocation().toVector()
                .subtract(attacker.getLocation().toVector()).normalize();
        direction.setY(0.35);
        victim.setVelocity(direction.multiply(kbMultiplier * 0.5));

        SoundUtil.play(victim.getLocation(), Sound.ENTITY_PLAYER_ATTACK_KNOCKBACK, 0.8f, 0.7f);
    }

    @Override
    public void onDamaged(EntityDamageByEntityEvent event, Player victim, Entity attacker, int level) {
        if (!isEnabled()) return;

        double reduction = cfgd("kb_resistance", 0.1 + level * 0.1);
        if (Math.random() < reduction) {
            victim.setVelocity(new Vector(0, 0, 0));
        }
    }

    @Override
    public String getDescription(int level) {
        double kb = 1.0 + level * 0.5;
        int res = (int) ((0.1 + level * 0.1) * 100);
        return "§7" + kb + "x knockback dealt. §b" + res + "% §7knockback resistance.";
    }
}
