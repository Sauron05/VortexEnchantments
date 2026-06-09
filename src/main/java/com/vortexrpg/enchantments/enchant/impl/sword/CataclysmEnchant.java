package com.vortexrpg.enchantments.enchant.impl.sword;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
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
 * Cataclysm: Right-click with full charge: slam the ground, creating a shockwave
 * dealing 6/8/10 hearts of damage in a 5-block radius. Cooldown: 30/25/20s.
 */
public class CataclysmEnchant extends VortexEnchant {

    public CataclysmEnchant() {
        super("cataclysm", "Cataclysm", EnchantRarity.MYTHIC, 3, List.of(ItemTarget.SWORD));
    }

    @Override
    public void onInteract(PlayerInteractEvent event, Player player, ItemStack item, int level) {
        if (!isEnabled()) return;
        if (!event.getAction().isRightClick()) return;
        if (isOnCooldown(player)) return;

        double cooldown = cfgd("cooldown_seconds", 35.0 - level * 5.0);
        double radius = cfgd("radius", 5.0);
        double damage = cfgd("damage", 10.0 + level * 4.0);

        setCooldownSeconds(player, cooldown);

        ParticleUtil.drawCircle(player.getLocation(), radius, 30, Particle.FLAME);
        ParticleUtil.spawn(player.getLocation(), Particle.EXPLOSION, 3, 1.0);
        SoundUtil.playExplosion(player.getLocation());
        SoundUtil.play(player.getLocation(), Sound.ENTITY_RAVAGER_ROAR, 1.0f, 0.8f);

        for (Entity e : player.getNearbyEntities(radius, radius, radius)) {
            if (!(e instanceof LivingEntity le)) continue;
            double dist = e.getLocation().distance(player.getLocation());
            double falloff = 1.0 - (dist / (radius * 1.5));
            if (falloff <= 0) continue;

            le.damage(damage * falloff, player);

            Vector kb = e.getLocation().toVector().subtract(player.getLocation().toVector()).normalize().multiply(1.2);
            kb.setY(0.6);
            e.setVelocity(kb);
        }

        player.sendMessage("§4[Cataclysm] §7Ground shattered! §c" + String.format("%.0f", damage / 2) + " hearts §7in " + String.format("%.0f", radius) + " blocks!");
    }

    @Override
    public String getDescription(int level) {
        double hearts = (10.0 + level * 4.0) / 2;
        return "§7Right-click: §4ground slam§7 dealing §c" + String.format("%.0f", hearts) + "\u2764§7 in 5-block radius.";
    }
}
