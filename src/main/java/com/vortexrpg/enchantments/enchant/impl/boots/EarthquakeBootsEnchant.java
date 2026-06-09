package com.vortexrpg.enchantments.enchant.impl.boots;

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
 * EarthquakeBoots: Sneak to cause an earthquake, knocking enemies up and dealing damage.
 */
public class EarthquakeBootsEnchant extends VortexEnchant {
    public EarthquakeBootsEnchant() { super("earthquake_boots", "Earthquake Boots", EnchantRarity.EPIC, 3, List.of(ItemTarget.BOOTS)); }

    @Override
    public void onToggleSneak(PlayerToggleSneakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!event.isSneaking()) return;
        if (isOnCooldown(player)) return;

        double radius = cfgd("radius", 5.0 + level);
        double dmg = cfgd("damage", 2.0 * level);
        for (LivingEntity e : MathUtil.getNearbyLiving(player.getLocation(), radius)) {
            if (e.equals(player)) continue;
            e.damage(dmg, player);
            e.setVelocity(e.getVelocity().setY(0.5 + level * 0.2));
        }
        ParticleUtil.drawCircle(player.getLocation().add(0, 0.1, 0), radius, 25, Particle.CAMPFIRE_COSY_SMOKE);
        SoundUtil.play(player.getLocation(), Sound.ENTITY_IRON_GOLEM_ATTACK, 1.0f, 0.4f);
        setCooldownFromConfig(player, "cooldown", 15.0);
    }

    @Override public String getDescription(int level) {
        return "§7Sneak: earthquake dealing §c" + (2 * level) + " §7damage + knockup. §815s CD.";
    }
}
