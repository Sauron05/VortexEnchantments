package com.vortexrpg.enchantments.enchant.impl.elytra;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.List;

/** AeroShield: Flat damage reduction while gliding. */
public class AeroShieldEnchant extends VortexEnchant {
    private static final double[] REDUCTION = {0.10, 0.15, 0.20};

    public AeroShieldEnchant() { super("aero_shield", "Aero Shield", EnchantRarity.RARE, 3, List.of(ItemTarget.ELYTRA)); }

    @Override
    public void onDamageTaken(EntityDamageEvent event, Player player, int level) {
        if (!isEnabled() || !player.isGliding()) return;
        double reduction = cfgd("reduction", REDUCTION[level - 1]);
        event.setDamage(event.getDamage() * (1.0 - reduction));
    }

    @Override public String getDescription() { return "Damage reduction while gliding."; }
    @Override public String getDescription(int level) {
        return "§7Take §a" + (int)(REDUCTION[level - 1] * 100) + "%§7 less damage while gliding."; }
}
