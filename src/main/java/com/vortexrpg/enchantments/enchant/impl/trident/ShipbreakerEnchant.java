package com.vortexrpg.enchantments.enchant.impl.trident;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/** Shipbreaker: 2×/2.5×/3× damage to entities riding vehicles. Vehicles destroyed in 1 hit. */
public class ShipbreakerEnchant extends VortexEnchant {
    private static final double[] MULT = {2.0, 2.5, 3.0};

    public ShipbreakerEnchant() { super("shipbreaker", "Shipbreaker", EnchantRarity.RARE, 3, List.of(ItemTarget.TRIDENT)); }

    private void apply(EntityDamageByEntityEvent event, Player attacker, LivingEntity target, int level) {
        if (!isEnabled()) return;
        if (target.getVehicle() == null) return;
        Entity vehicle = target.getVehicle();
        if (vehicle instanceof Boat || vehicle instanceof Minecart) {
            vehicle.remove();
        }
        event.setDamage(event.getDamage() * MULT[level-1]);
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity target, int level) {
        apply(event, attacker, target, level);
    }

    @Override
    public void onTridentHit(EntityDamageByEntityEvent event, Player thrower, LivingEntity target, int level) {
        apply(event, thrower, target, level);
    }

    @Override public String getDescription() { return "Massively damages vehicle riders and destroys vehicles."; }
    @Override public String getDescription(int level) {
        return "§c" + MULT[level-1] + "×§7 damage to vehicle riders. Vehicles destroyed instantly."; }
}
