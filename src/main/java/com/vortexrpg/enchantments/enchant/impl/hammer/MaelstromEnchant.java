package com.vortexrpg.enchantments.enchant.impl.hammer;

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
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

/**
 * Maelstrom: Right-click for a 360-degree spin attack in 3/4/5 block radius.
 * 8-second cooldown.
 */
public class MaelstromEnchant extends VortexEnchant {

    public MaelstromEnchant() {
        super("maelstrom", "Maelstrom", EnchantRarity.EPIC, 3, List.of(ItemTarget.HAMMER));
    }

    @Override
    public void onInteract(PlayerInteractEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!event.getAction().isRightClick()) return;
        if (isOnCooldown(player)) return;

        double radius = cfgd("radius", 2.0 + level);
        double damage = cfgd("damage", 3.0 + level * 2.0);

        for (LivingEntity entity : MathUtil.getNearbyLiving(player.getLocation(), radius)) {
            if (entity.equals(player)) continue;
            entity.damage(damage, player);
        }

        ParticleUtil.drawCircle(player.getLocation().add(0, 0.5, 0), radius, 24, Particle.SWEEP_ATTACK);
        SoundUtil.play(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.0f, 0.8f);
        setCooldownFromConfig(player, "cooldown", 8);
    }

    @Override
    public String getDescription(int level) {
        int r = 2 + level;
        double d = 3 + level * 2;
        return "§7Right-click: §e360° spin §7— §c" + d + " dmg §7in " + r + " blocks. §8(8s CD)";
    }
}
