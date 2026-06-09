package com.vortexrpg.enchantments.enchant.impl.helmet;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

import java.util.List;

/** Crown: When hit, 20/25/30% chance to knock the attacker back. */
public class CrownEnchant extends VortexEnchant {
    private static final double[] CHANCE = {0.20, 0.25, 0.30};

    public CrownEnchant() { super("crown", "Crown", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.HELMET)); }

    @Override
    public void onDamageTaken(EntityDamageByEntityEvent event, Player player, int level) {
        if (!isEnabled()) return;
        double chance = cfg("chance", CHANCE[level-1]);
        if (Math.random() < chance && event.getDamager() instanceof LivingEntity attacker) {
            Vector kb = attacker.getLocation().toVector().subtract(player.getLocation().toVector())
                .normalize().multiply(1.5).setY(0.4);
            attacker.setVelocity(kb);
        }
    }

    @Override public String getDescription() { return "Chance to knock back attackers."; }
    @Override public String getDescription(int level) {
        return "§a" + (int)(CHANCE[level-1]*100) + "§a%§7 chance to knock back attackers."; }
}
