package com.vortexrpg.enchantments.enchant.impl.hoe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Phantom;
import org.bukkit.entity.Player;

import java.util.List;

/** Scarecrow: Phantoms flee and won't attack within 12/16/20 blocks while hoe is in hotbar. */
public class ScarecrowEnchant extends VortexEnchant {
    private static final double[] RADIUS = {12, 16, 20};

    public ScarecrowEnchant() { super("scarecrow", "Scarecrow", EnchantRarity.RARE, 3, List.of(ItemTarget.HOE)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        double radius = cfg("radius", RADIUS[level-1]);
        player.getWorld().getNearbyEntities(player.getLocation(), radius, radius, radius, e ->
            e.getType() == EntityType.PHANTOM).forEach(e -> {
            Phantom phantom = (Phantom) e;
            phantom.setTarget(null);
            // Nudge the phantom away
            var dir = phantom.getLocation().toVector().subtract(player.getLocation().toVector()).normalize().multiply(0.5);
            phantom.setVelocity(phantom.getVelocity().add(dir));
        });
    }

    @Override public String getDescription() { return "Phantoms won't attack you while held."; }
    @Override public String getDescription(int level) {
        return "§7Phantoms within §a" + (int)RADIUS[level-1] + " §7blocks flee."; }
}
