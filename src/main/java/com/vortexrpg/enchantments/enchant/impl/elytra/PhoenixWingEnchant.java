package com.vortexrpg.enchantments.enchant.impl.elytra;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.List;

/** PhoenixWing: Once per cooldown, prevent lethal damage with a fire burst. */
public class PhoenixWingEnchant extends VortexEnchant {

    public PhoenixWingEnchant() { super("phoenix_wing", "Phoenix Wing", EnchantRarity.EPIC, 3, List.of(ItemTarget.ELYTRA)); }

    @Override
    public void onDamageTaken(EntityDamageEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (player.getHealth() - event.getFinalDamage() > 0) return;
        if (isOnCooldown(player)) return;
        int cooldown = cfgi("cooldown", Math.max(60, 360 - level * 60));
        setCooldownSeconds(player, cooldown);
        event.setCancelled(true);
        player.setHealth(cfgd("revive_health", 2.0 + level * 2));
        player.getWorld().spawnParticle(Particle.FLAME, player.getLocation(), 60, 1.5, 1.5, 1.5, 0.05);
        player.getWorld().playSound(player.getLocation(), Sound.ITEM_TOTEM_USE, 1.0f, 1.2f);
    }

    @Override public String getDescription() { return "Survive lethal damage with a phoenix burst."; }
    @Override public String getDescription(int level) {
        return "§7Prevents lethal damage once per §e" + Math.max(60, 360 - level * 60) + "s§7. Restores §c" + (int)(2.0 + level * 2) + "\u2764§7."; }
}
