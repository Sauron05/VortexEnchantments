package com.vortexrpg.enchantments.enchant.impl.chestplate;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/** Fortify: Each kill adds a stack (+2% max DR); stacks reset on death. Max 10 stacks. */
public class FortifyEnchant extends VortexEnchant {
    private static final double PER_STACK = 0.02;
    private static final int MAX = 10;

    public FortifyEnchant() { super("fortify", "Fortify", EnchantRarity.EPIC, 3, List.of(ItemTarget.CHESTPLATE)); }

    @Override
    public void onKill(EntityDamageByEntityEvent event, Player player, org.bukkit.entity.LivingEntity killed, int level) {
        if (!isEnabled()) return;
        int stacks = plugin.getPlayerDataManager().getFortifyStacks(player.getUniqueId());
        if (stacks < MAX * level) {
            plugin.getPlayerDataManager().setFortifyStacks(player.getUniqueId(), stacks + 1);
        }
    }

    @Override
    public void onDamageTaken(EntityDamageByEntityEvent event, Player player, int level) {
        if (!isEnabled()) return;
        int stacks = plugin.getPlayerDataManager().getFortifyStacks(player.getUniqueId());
        double reduce = Math.min(PER_STACK * stacks, 0.40);
        event.setDamage(event.getDamage() * (1.0 - reduce));
    }

    @Override
    public void onRespawn(Player player, int level) {
        plugin.getPlayerDataManager().setFortifyStacks(player.getUniqueId(), 0);
    }

    @Override public String getDescription() { return "Kills build DR stacks; resets on death."; }
    @Override public String getDescription(int level) {
        return "§7Each kill: §a+2%§7 DR (max §a" + (MAX*level) + "§7 stacks, resets on death)."; }
}
