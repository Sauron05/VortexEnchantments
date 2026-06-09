package com.vortexrpg.enchantments.enchant.impl.hammer;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Gigantify: Right-click to grow — increased reach, +damage, reduced speed for 8 seconds.
 * 20-second cooldown. Uses scale attribute or potion effects.
 */
public class GigantifyEnchant extends VortexEnchant {

    private static final Set<UUID> ACTIVE = ConcurrentHashMap.newKeySet();

    public GigantifyEnchant() {
        super("gigantify", "Gigantify", EnchantRarity.EPIC, 3, List.of(ItemTarget.HAMMER));
    }

    @Override
    public void onInteract(PlayerInteractEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!event.getAction().isRightClick()) return;
        if (isOnCooldown(player)) return;
        if (ACTIVE.contains(player.getUniqueId())) return;

        ACTIVE.add(player.getUniqueId());

        int durationTicks = cfgi("duration_ticks", 160);
        int strengthAmp = level - 1;

        player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, durationTicks, strengthAmp, false, true));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, durationTicks, 1, false, true));
        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, durationTicks, 0, false, true));

        ParticleUtil.spawn(player.getLocation(), Particle.CLOUD, 15, 0.8);
        SoundUtil.play(player.getLocation(), Sound.ENTITY_IRON_GOLEM_REPAIR, 1.0f, 0.5f);

        new BukkitRunnable() {
            @Override
            public void run() {
                ACTIVE.remove(player.getUniqueId());
            }
        }.runTaskLater(plugin, durationTicks);

        setCooldownFromConfig(player, "cooldown", 20);
    }

    @Override
    public String getDescription(int level) {
        return "§7Right-click: §dGrow §7— Strength " + level + ", Slowness, Resistance §e8s§7. §8(20s CD)";
    }
}
