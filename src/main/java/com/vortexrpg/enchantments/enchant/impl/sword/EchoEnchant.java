package com.vortexrpg.enchantments.enchant.impl.sword;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * Echo: Hit replays as a phantom strike after 1.5/1.25/1.0 seconds dealing 60% of original damage.
 * Uses a flag to prevent chaining.
 */
public class EchoEnchant extends VortexEnchant {

    private static final double[] DELAY_SECS = {1.5, 1.25, 1.0};

    public EchoEnchant() {
        super("echo", "Echo", EnchantRarity.EPIC, 3, List.of(ItemTarget.SWORD));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        // Prevent chaining: if this HIT is already an echo, skip
        if (plugin.getPlayerDataManager().getInt(attacker.getUniqueId(), "echo_active") == 1) return;

        double delaySecs = cfg("delay_seconds", DELAY_SECS[level - 1]);
        double echoPct = cfg("echo_damage_percent", 60.0) / 100.0;
        double echoDamage = event.getDamage() * echoPct;
        long delayTicks = Math.max(1L, (long)(delaySecs * 20));

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (!victim.isValid() || victim.isDead()) return;
            plugin.getPlayerDataManager().setInt(attacker.getUniqueId(), "echo_active", 1);
            victim.damage(echoDamage, attacker);
            plugin.getPlayerDataManager().setInt(attacker.getUniqueId(), "echo_active", 0);
        }, delayTicks);
    }

    @Override
    public String getDescription() { return "Your hit is replayed as a phantom strike seconds later."; }

    @Override
    public String getDescription(int level) {
        return "§7After §e" + DELAY_SECS[level - 1] + "s§7: phantom strike deals §c60%§7 of original hit.";
    }
}
