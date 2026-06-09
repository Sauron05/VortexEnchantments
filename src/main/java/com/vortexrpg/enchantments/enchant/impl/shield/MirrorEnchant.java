package com.vortexrpg.enchantments.enchant.impl.shield;

import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.Random;

/**
 * Mirror — Shield (Epic, Max 3)
 * 10/15/20% chance to copy and reflect the blocked damage type back to the attacker.
 */
public class MirrorEnchant extends VortexEnchant {

    private static final Random RNG = new Random();

    public MirrorEnchant() {
        super("mirror", "Mirror", "shield");
    }

    @Override
    public String getTier() { return "EPIC"; }

    @Override
    public int getMaxLevel() { return 3; }

    @Override
    public String getDescription(int level) {
        int[] chances = {10, 15, 20};
        return "§e" + chances[level - 1] + "%§7 chance when blocking to reflect the exact damage back at the attacker.";
    }

    @Override
    public void onDamageTaken(EntityDamageByEntityEvent event, Player player, int level) {
        if (!player.isBlocking()) return;
        Entity attacker = event.getDamager();
        if (!(attacker instanceof LivingEntity le)) return;

        double[] chances = {0.10, 0.15, 0.20};
        if (RNG.nextDouble() < cfgd("chance", chances[level - 1])) {
            double reflected = event.getFinalDamage();
            plugin.getServer().getScheduler().runTask(plugin, () -> le.damage(reflected, player));
        }
    }
}
