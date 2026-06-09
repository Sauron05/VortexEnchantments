package com.vortexrpg.enchantments.enchant.impl.elytra;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;

/** ThermalSurge: Near lava or fire, get an upward velocity boost while gliding. */
public class ThermalSurgeEnchant extends VortexEnchant {

    public ThermalSurgeEnchant() { super("thermal_surge", "Thermal Surge", EnchantRarity.RARE, 3, List.of(ItemTarget.ELYTRA)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled() || !player.isGliding()) return;
        int scanRadius = cfgi("scan_radius", 5);
        boolean nearHeat = false;
        Block center = player.getLocation().getBlock();
        for (int x = -scanRadius; x <= scanRadius && !nearHeat; x++) {
            for (int y = -scanRadius; y <= scanRadius && !nearHeat; y++) {
                for (int z = -scanRadius; z <= scanRadius && !nearHeat; z++) {
                    Material mat = center.getRelative(x, y, z).getType();
                    if (mat == Material.LAVA || mat == Material.FIRE || mat == Material.SOUL_FIRE || mat == Material.MAGMA_BLOCK) {
                        nearHeat = true;
                    }
                }
            }
        }
        if (nearHeat) {
            double lift = cfgd("lift", 0.1 + level * 0.05);
            player.setVelocity(player.getVelocity().add(new Vector(0, lift, 0)));
        }
    }

    @Override public String getDescription() { return "Heat sources boost you upward while gliding."; }
    @Override public String getDescription(int level) {
        return "§7Near §clava§7/§6fire§7: strong upward boost while gliding."; }
}
