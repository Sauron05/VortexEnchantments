package com.vortexrpg.enchantments.enchant.impl.elytra;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/** Ascendant: After prolonged gliding, gain extreme buffs. */
public class AscendantEnchant extends VortexEnchant {

    public AscendantEnchant() { super("ascendant", "Ascendant", EnchantRarity.MYTHIC, 1, List.of(ItemTarget.ELYTRA)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        if (player.isGliding()) {
            int seconds = plugin.getPlayerDataManager().getInt(player.getUniqueId(), "ascendant_s", 0) + 1;
            plugin.getPlayerDataManager().setInt(player.getUniqueId(), "ascendant_s", seconds);
            int threshold = cfgi("threshold", 10);
            if (seconds == threshold) {
                int dur = cfgi("buff_duration", 8) * 20;
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, dur, 2, true, false, true));
                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, dur, 1, true, false, true));
                player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, dur, 2, true, false, true));
                player.setInvulnerable(true);
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> player.setInvulnerable(false), dur);
                player.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, player.getLocation(), 50, 1, 1, 1, 0.3);
                player.getWorld().playSound(player.getLocation(), Sound.ITEM_TOTEM_USE, 1.0f, 1.0f);
            }
        } else {
            plugin.getPlayerDataManager().setInt(player.getUniqueId(), "ascendant_s", 0);
        }
    }

    @Override public String getDescription() { return "Prolonged flight grants godlike power."; }
    @Override public String getDescription(int level) {
        return "§7After §e10s§7 of flight: §dImmunity + Speed III + Regen II + Resistance III§7 for §e8s§7."; }
}
