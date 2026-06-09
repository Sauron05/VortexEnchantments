package com.vortexrpg.enchantments.enchant.impl.chestplate;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.util.List;

/**
 * Novaplate: Sneak to charge, release AoE damage burst.
 */
public class NovaplateEnchant extends VortexEnchant {
    public NovaplateEnchant() { super("novaplate", "Novaplate", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.CHESTPLATE)); }

    @Override
    public void onToggleSneak(PlayerToggleSneakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!event.isSneaking()) return;
        if (isOnCooldown(player)) return;

        double radius = cfgd("radius", 5.0 + level * 2.0);
        double dmg = cfgd("damage", 4.0 + level * 3.0);

        for (LivingEntity e : MathUtil.getNearbyLiving(player.getLocation(), radius)) {
            if (e.equals(player)) continue;
            e.damage(dmg, player);
        }
        ParticleUtil.burst(player.getLocation(), Particle.EXPLOSION, 3, 2.0);
        ParticleUtil.drawCircle(player.getLocation().add(0, 0.1, 0), radius, 30, Particle.FLAME);
        SoundUtil.play(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 0.6f);
        setCooldownFromConfig(player, "cooldown", 25.0);
    }

    @Override public String getDescription(int level) {
        return "§7Sneak: §cNova burst §7dealing " + (int)(4 + level * 3) + " damage in " + (int)(5 + level * 2) + " blocks. §825s CD.";
    }
}
