package com.vortexrpg.enchantments.enchant.impl.axe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Sever: On kill, drops orbit the kill point for 5s before becoming collectable.
 */
public class SeverEnchant extends VortexEnchant {

    public SeverEnchant() {
        super("sever", "Sever", EnchantRarity.UNCOMMON, 1, List.of(ItemTarget.AXE));
    }

    @Override
    public void onKill(EntityDeathEvent event, Player killer, LivingEntity killed, int level) {
        if (!isEnabled()) return;
        double orbitSecs = cfg("orbit_duration_seconds", 5.0);

        // Capture drops before they actually drop
        List<ItemStack> drops = new ArrayList<>(event.getDrops());
        event.getDrops().clear();

        Random rng = new Random();
        for (ItemStack drop : drops) {
            Item item = killed.getWorld().dropItem(killed.getLocation(), drop);
            item.setPickupDelay((int)(orbitSecs * 20));
            // Give orbit velocity
            double angle = rng.nextDouble() * Math.PI * 2;
            Vector vel = new Vector(Math.cos(angle) * 0.15, 0.15, Math.sin(angle) * 0.15);
            item.setVelocity(vel);
        }
    }

    @Override
    public String getDescription() { return "Drops orbit the kill point before they can be picked up."; }

    @Override
    public String getDescription(int level) {
        return "§7On kill: drops orbit §e5s§7 before becoming collectable.";
    }
}
