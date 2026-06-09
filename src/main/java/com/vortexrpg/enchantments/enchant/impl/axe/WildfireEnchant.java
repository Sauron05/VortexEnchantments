package com.vortexrpg.enchantments.enchant.impl.axe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * Wildfire: Attacks set the target and nearby enemies on fire.
 * Area fire spread: 3/4/5 block radius. Fire ticks: 40/60/80.
 */
public class WildfireEnchant extends VortexEnchant {

    public WildfireEnchant() {
        super("wildfire", "Wildfire", EnchantRarity.EPIC, 3, List.of(ItemTarget.AXE));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        int fireTicks = cfgi("fire_ticks", 20 + level * 20);
        double radius = cfgd("radius", 2.0 + level);

        victim.setFireTicks(fireTicks);

        Location loc = victim.getLocation();
        for (LivingEntity nearby : MathUtil.getNearbyLiving(loc, radius)) {
            if (nearby.equals(attacker)) continue;
            nearby.setFireTicks(fireTicks / 2);
        }

        ParticleUtil.spawn(loc, Particle.FLAME, 25, radius * 0.5);
        ParticleUtil.spawn(loc, Particle.LAVA, 8, radius * 0.3);
        SoundUtil.play(loc, Sound.ITEM_FIRECHARGE_USE, 0.8f, 0.7f);
    }

    @Override
    public String getDescription(int level) {
        double secs = (20 + level * 20) / 20.0;
        int r = (int) (2 + level);
        return "§7Sets target on fire for §c" + secs + "s §7and spreads to enemies within §e" + r + " blocks§7.";
    }
}
