package com.vortexrpg.enchantments.enchant.impl.boots;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

import java.util.List;

/** Anchor: When hit, pull attacker toward you 1/1.5/2 blocks. */
public class AnchorEnchant extends VortexEnchant {
    private static final double[] PULL = {1.0, 1.5, 2.0};

    public AnchorEnchant() { super("anchor", "Anchor", EnchantRarity.RARE, 3, List.of(ItemTarget.BOOTS)); }

    @Override
    public void onDamageTaken(EntityDamageByEntityEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!(event.getDamager() instanceof LivingEntity attacker)) return;
        double pullStr = cfg("pull", PULL[level-1]);
        Vector dir = player.getLocation().toVector().subtract(attacker.getLocation().toVector()).normalize().multiply(pullStr);
        attacker.setVelocity(attacker.getVelocity().add(dir));
    }

    @Override public String getDescription() { return "Being hit pulls attackers toward you."; }
    @Override public String getDescription(int level) {
        return "§7Melee hits pull attacker §a" + PULL[level-1] + "§7 blocks toward you."; }
}
