package com.vortexrpg.enchantments.enchant.impl.elytra;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.List;

/** AirCushion: Reduces fall damage by 20/35/50%. */
public class AirCushionEnchant extends VortexEnchant {
    private static final double[] REDUCTION = {0.20, 0.35, 0.50};

    public AirCushionEnchant() { super("air_cushion", "Air Cushion", EnchantRarity.COMMON, 3, List.of(ItemTarget.ELYTRA)); }

    @Override
    public void onDamageTaken(EntityDamageEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            double reduction = cfgd("reduction", REDUCTION[level - 1]);
            event.setDamage(event.getDamage() * (1.0 - reduction));
        }
    }

    @Override public String getDescription() { return "Reduces fall damage after flight."; }
    @Override public String getDescription(int level) {
        return "§7Reduces fall damage by §a" + (int)(REDUCTION[level - 1] * 100) + "%§7."; }
}
