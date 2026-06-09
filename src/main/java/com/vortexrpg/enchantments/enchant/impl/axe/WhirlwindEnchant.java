package com.vortexrpg.enchantments.enchant.impl.axe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Whirlwind: Right-click to spin, dealing damage to all enemies within 3/4/5 blocks
 * and pulling them slightly inward. 360-degree axe spin attack.
 */
public class WhirlwindEnchant extends VortexEnchant {

    public WhirlwindEnchant() {
        super("whirlwind", "Whirlwind", EnchantRarity.EPIC, 3, List.of(ItemTarget.AXE));
    }

    @Override
    public void onInteract(PlayerInteractEvent event, Player player, ItemStack item, int level) {
        if (!isEnabled()) return;
        if (!event.getAction().isRightClick()) return;
        if (isOnCooldown(player)) return;

        double cooldown = cfgd("cooldown_seconds", 6.0);
        double radius = cfgd("radius", 2.0 + level);
        double damage = cfgd("damage", 3.0 + level * 1.5);

        setCooldownSeconds(player, cooldown);

        Location center = player.getLocation();
        ParticleUtil.drawCircle(center.clone().add(0, 1, 0), radius, 24, Particle.SWEEP_ATTACK);
        SoundUtil.play(center, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.0f, 0.8f);

        for (Entity e : player.getNearbyEntities(radius, radius, radius)) {
            if (!(e instanceof LivingEntity le)) continue;
            le.damage(damage, player);
            Vector pull = center.toVector().subtract(e.getLocation().toVector()).normalize().multiply(0.3);
            e.setVelocity(e.getVelocity().add(pull));
        }

        player.sendMessage("§6[Whirlwind] §7Spinning strike!");
    }

    @Override
    public String getDescription(int level) {
        double rad = 2.0 + level;
        return "§7Right-click: §6spin attack§7 hitting all within §e" + String.format("%.0f", rad) + " blocks§7.";
    }
}
