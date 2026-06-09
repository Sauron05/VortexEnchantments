package com.vortexrpg.enchantments.enchant.impl.sword;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.potion.*;

import java.util.List;

/**
 * Mimic: On kill, gain a 5s buff matching the mob type killed.
 */
public class MimicEnchant extends VortexEnchant {

    public MimicEnchant() {
        super("mimic", "Mimic", EnchantRarity.LEGENDARY, 1, List.of(ItemTarget.SWORD));
    }

    @Override
    public void onKill(EntityDeathEvent event, Player killer, LivingEntity killed, int level) {
        if (!isEnabled()) return;
        int durationTicks = cfgi("buff_duration_ticks", 100);

        applyMimicBuff(killer, killed, durationTicks);
        ParticleUtil.spawn(killed.getLocation().add(0, 1, 0), Particle.ENTITY_EFFECT, 20, 0.4, Color.fromRGB(170, 0, 170));
        SoundUtil.play(killed.getLocation(), Sound.ENTITY_ILLUSIONER_CAST_SPELL, 0.8f, 1.2f);
    }

    private void applyMimicBuff(Player player, LivingEntity killed, int ticks) {
        switch (killed.getType()) {
            case ZOMBIE -> apply(player, PotionEffectType.STRENGTH, ticks, 0);
            case SKELETON -> apply(player, PotionEffectType.SPEED, ticks, 1);
            case SPIDER, CAVE_SPIDER -> apply(player, PotionEffectType.JUMP_BOOST, ticks, 1);
            case BLAZE -> apply(player, PotionEffectType.FIRE_RESISTANCE, ticks, 0);
            case ENDERMAN -> {
                // Next hit teleports behind target — store flag
                plugin.getPlayerDataManager().setInt(player.getUniqueId(), "mimic_enderman", 1);
                plugin.getServer().getScheduler().runTaskLater(plugin, () ->
                    plugin.getPlayerDataManager().setInt(player.getUniqueId(), "mimic_enderman", 0), ticks);
            }
            case CREEPER -> apply(player, PotionEffectType.RESISTANCE, ticks, 0);
            case WITHER_SKELETON -> {
                // Wither aura — periodic wither damage to nearby mobs
                plugin.getPlayerDataManager().setInt(player.getUniqueId(), "mimic_wither_aura", 1);
                plugin.getServer().getScheduler().runTaskLater(plugin, () ->
                    plugin.getPlayerDataManager().setInt(player.getUniqueId(), "mimic_wither_aura", 0), ticks);
                // Start aura tick
                final int[] remaining = {ticks};
                plugin.getServer().getScheduler().runTaskTimer(plugin, task -> {
                    if (remaining[0] <= 0 || !player.isOnline()) { task.cancel(); return; }
                    if (plugin.getPlayerDataManager().getInt(player.getUniqueId(), "mimic_wither_aura") == 0) {
                        task.cancel(); return;
                    }
                    player.getWorld().getNearbyLivingEntities(player.getLocation(), 3).stream()
                        .filter(e -> e != player)
                        .forEach(e -> e.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 40, 0)));
                    remaining[0] -= 20;
                }, 20, 20);
            }
            case GUARDIAN, ELDER_GUARDIAN -> {
                apply(player, PotionEffectType.WATER_BREATHING, ticks, 0);
                apply(player, PotionEffectType.DOLPHINS_GRACE, ticks, 0);
            }
            case PHANTOM -> {
                apply(player, PotionEffectType.SLOW_FALLING, ticks, 0);
                apply(player, PotionEffectType.SPEED, ticks, 0);
            }
            default -> apply(player, PotionEffectType.REGENERATION, ticks, 0);
        }
    }

    private void apply(Player player, PotionEffectType type, int ticks, int amplifier) {
        player.addPotionEffect(new PotionEffect(type, ticks, amplifier, false, true));
    }

    @Override
    public String getDescription() { return "On kill, gain a 5s buff matching the slain mob type."; }

    @Override
    public String getDescription(int level) {
        return "Gain a §e5s §7buff mimicking your kill. Zombie=Strength, Skeleton=Speed, etc.";
    }
}
