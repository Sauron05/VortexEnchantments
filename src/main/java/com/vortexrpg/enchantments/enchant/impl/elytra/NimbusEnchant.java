package com.vortexrpg.enchantments.enchant.impl.elytra;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/** Nimbus: Allies near your flight path gain Speed. */
public class NimbusEnchant extends VortexEnchant {

    public NimbusEnchant() { super("nimbus", "Nimbus", EnchantRarity.RARE, 3, List.of(ItemTarget.ELYTRA)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled() || !player.isGliding()) return;
        double radius = cfgd("radius", 3.0 + level);
        int duration = cfgi("buff_duration", 40);
        for (Entity e : player.getWorld().getNearbyEntities(player.getLocation(), radius, radius, radius)) {
            if (e == player || !(e instanceof Player ally)) continue;
            ally.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration, 0, true, false, true));
        }
    }

    @Override public String getDescription() { return "Allies near your flight path gain Speed."; }
    @Override public String getDescription(int level) {
        return "§7Nearby allies within §e" + (int)(3.0 + level) + "§7 blocks gain §aSpeed I§7."; }
}
