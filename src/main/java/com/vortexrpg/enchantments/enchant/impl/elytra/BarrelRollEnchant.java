package com.vortexrpg.enchantments.enchant.impl.elytra;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.List;

/** BarrelRoll: Chance to deflect projectile damage while gliding. */
public class BarrelRollEnchant extends VortexEnchant {
    private static final double[] CHANCE = {0.10, 0.15, 0.20};

    public BarrelRollEnchant() { super("barrel_roll", "Barrel Roll", EnchantRarity.RARE, 3, List.of(ItemTarget.ELYTRA)); }

    @Override
    public void onDamageTaken(EntityDamageEvent event, Player player, int level) {
        if (!isEnabled() || !player.isGliding()) return;
        if (event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
            if (Math.random() < cfgd("chance", CHANCE[level - 1])) {
                event.setCancelled(true);
            }
        }
    }

    @Override public String getDescription() { return "Dodge projectiles while gliding."; }
    @Override public String getDescription(int level) {
        return "§a" + (int)(CHANCE[level - 1] * 100) + "%§7 chance to deflect projectile damage while gliding."; }
}
