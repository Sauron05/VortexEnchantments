package com.vortexrpg.enchantments.enchant.impl.boots;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;

import java.util.List;

/** Nomad: Every 100/80/60 blocks away from spawn, gain +1% damage (max +10%). */
public class NomadEnchant extends VortexEnchant {
    private static final int[] PER_BLOCKS = {100, 80, 60};
    private static final double MAX_BONUS = 0.10;

    public NomadEnchant() { super("nomad", "Nomad", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.BOOTS)); }

    @Override
    public void onAttack(org.bukkit.event.entity.EntityDamageByEntityEvent event, Player player, org.bukkit.entity.LivingEntity target, int level) {
        if (!isEnabled()) return;
        double dist = player.getLocation().distance(player.getWorld().getSpawnLocation());
        int perBlocks = cfgi("per_blocks", PER_BLOCKS[level-1]);
        double bonus = Math.min((dist / perBlocks) * 0.01, MAX_BONUS);
        if (bonus > 0) event.setDamage(event.getDamage() * (1.0 + bonus));
    }

    @Override public String getDescription() { return "Further from spawn = more damage."; }
    @Override public String getDescription(int level) {
        return "§7Every §a" + PER_BLOCKS[level-1] + "§7 blocks from spawn: §a+1%§7 damage (max §a10%§7)."; }
}
