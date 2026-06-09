package com.vortexrpg.enchantments.enchant.impl.shield;

import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 * Phalanx — Shield (Rare, Max 3)
 * Each nearby player holding a shield within radius reduces your damage taken by 5%.
 */
public class PhalanxEnchant extends VortexEnchant {

    public PhalanxEnchant() {
        super("phalanx", "Phalanx", "shield");
    }

    @Override
    public String getTier() { return "RARE"; }

    @Override
    public int getMaxLevel() { return 3; }

    @Override
    public String getDescription(int level) {
        double[] radii = {6, 8, 10};
        return "Each nearby ally with a shield (§e" + (int)radii[level - 1] + " blocks§7) reduces your damage taken by §a5%§7.";
    }

    @Override
    public void onDamageTaken(EntityDamageByEntityEvent event, Player player, int level) {
        double[] radii = {6, 8, 10};
        double radius = cfgd("radius", radii[level - 1]);
        double perAlly = cfgd("per_ally_reduce", 0.05);

        int allies = 0;
        for (Player nearby : player.getWorld().getPlayers()) {
            if (nearby == player) continue;
            if (nearby.getLocation().distanceSquared(player.getLocation()) > radius * radius) continue;
            ItemStack offhand = nearby.getInventory().getItemInOffHand();
            if (offhand.getType() == Material.SHIELD) allies++;
        }

        if (allies > 0) {
            double reduction = Math.min(allies * perAlly, 0.75); // cap at 75%
            event.setDamage(event.getDamage() * (1.0 - reduction));
        }
    }
}
