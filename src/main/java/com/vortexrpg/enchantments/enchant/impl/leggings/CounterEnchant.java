package com.vortexrpg.enchantments.enchant.impl.leggings;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/** Counter: Each incoming hit increases your next outgoing attack by 5/8/12%. Stacks up to 3. */
public class CounterEnchant extends VortexEnchant {
    private static final double[] PER_HIT = {0.05, 0.08, 0.12};
    private static final int MAX_STACKS = 3;

    public CounterEnchant() { super("counter", "Counter", EnchantRarity.RARE, 3, List.of(ItemTarget.LEGGINGS)); }

    @Override
    public void onDamageTaken(EntityDamageByEntityEvent event, Player player, int level) {
        if (!isEnabled()) return;
        int stacks = Math.min(plugin.getPlayerDataManager().getInt(player.getUniqueId(), "counter_stacks") + 1, MAX_STACKS);
        plugin.getPlayerDataManager().setInt(player.getUniqueId(), "counter_stacks", stacks);
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player player, LivingEntity target, int level) {
        if (!isEnabled()) return;
        int stacks = plugin.getPlayerDataManager().getInt(player.getUniqueId(), "counter_stacks");
        if (stacks == 0) return;
        double bonus = cfg("per_hit", PER_HIT[level-1]) * stacks;
        event.setDamage(event.getDamage() * (1.0 + bonus));
        plugin.getPlayerDataManager().setInt(player.getUniqueId(), "counter_stacks", 0);
    }

    @Override public String getDescription() { return "Hits received power your next strike."; }
    @Override public String getDescription(int level) {
        return "§7Each hit received: §a+" + (int)(PER_HIT[level-1]*100) + "§a%§7 to next attack (max " + MAX_STACKS + " stacks)."; }
}
