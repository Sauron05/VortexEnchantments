package com.vortexrpg.enchantments.enchant.impl.crossbow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Failsafe (Mythic): When lethal damage is taken with a loaded bolt, auto-fire at attacker, survive on 1 HP. 3min cooldown.
 */
public class FailsafeEnchant extends VortexEnchant {
    public FailsafeEnchant() { super("failsafe", "Failsafe", EnchantRarity.MYTHIC, 1, List.of(ItemTarget.CROSSBOW)); }

    @Override
    public void onDamageTaken(EntityDamageEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!(event instanceof org.bukkit.event.entity.EntityDamageByEntityEvent edbee)) return;
        if (player.getHealth() - event.getFinalDamage() > 0) return; // not lethal
        if (isOnCooldown(player)) return;

        // Check if crossbow is in hand
        ItemStack hand = player.getInventory().getItemInMainHand();
        if (hand.getType() != org.bukkit.Material.CROSSBOW) return;

        // Auto-fire at attacker
        Entity attacker = edbee.getDamager();
        if (attacker instanceof LivingEntity target) {
            Arrow arrow = player.getWorld().spawnArrow(
                player.getEyeLocation(),
                target.getEyeLocation().toVector().subtract(player.getEyeLocation().toVector()).normalize(),
                2.5f, 0f);
            arrow.setShooter(player);
            arrow.setDamage(cfg("auto_fire_damage", 8.0));
        }

        event.setCancelled(true);
        player.setHealth(1.0);
        SoundUtil.play(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1f, 1.5f);
        setCooldown(player, 3 * 60 * 1000L);
    }

    @Override public String getDescription() { return "Survive lethal damage and auto-fire at attacker."; }
    @Override public String getDescription(int level) {
        return "§7Lethal damage: §csurve on §41♥§c§7, auto-fires bolt at attacker. §e3-min cooldown§7."; }
}
