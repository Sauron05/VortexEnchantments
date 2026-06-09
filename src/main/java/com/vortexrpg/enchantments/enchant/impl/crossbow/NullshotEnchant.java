package com.vortexrpg.enchantments.enchant.impl.crossbow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.enchantments.Enchantment;

import java.util.List;
import java.util.Map;

/**
 * Nullshot: Bolt temporarily disables all enchantments on the target's held items
 * for 3/5/7 seconds. Anti-enchant warfare.
 * Implemented by storing and removing enchants, restoring them after duration.
 */
public class NullshotEnchant extends VortexEnchant {

    public NullshotEnchant() {
        super("nullshot", "Nullshot", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.CROSSBOW));
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        if (isOnCooldown(shooter)) return;

        if (!(victim instanceof Player target)) return;

        int duration = cfgi("duration", 1 + level * 2) * 20;
        ItemStack mainHand = target.getInventory().getItemInMainHand();

        if (mainHand.getType().isAir()) return;

        Map<Enchantment, Integer> stored = Map.copyOf(mainHand.getEnchantments());
        if (stored.isEmpty()) return;

        for (Enchantment ench : stored.keySet()) {
            mainHand.removeEnchantment(ench);
        }

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            ItemStack current = target.getInventory().getItemInMainHand();
            if (current.equals(mainHand)) {
                stored.forEach((e, l) -> current.addUnsafeEnchantment(e, l));
            }
        }, duration);

        ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.WITCH, 15, 0.5);
        SoundUtil.play(victim.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 1.0f, 0.5f);

        setCooldownFromConfig(shooter, "cooldown", 15.0);
    }

    @Override
    public String getDescription(int level) {
        int dur = 1 + level * 2;
        return "§7Bolt §c§ldisables §7target's enchants for §e" + dur + "s§7. 15s CD.";
    }
}
