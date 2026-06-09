package com.vortexrpg.enchantments.enchant.impl.elytra;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/** SoftLanding: Grants Slow Falling when you stop gliding. */
public class SoftLandingEnchant extends VortexEnchant {

    public SoftLandingEnchant() { super("soft_landing", "Soft Landing", EnchantRarity.COMMON, 3, List.of(ItemTarget.ELYTRA)); }

    @SuppressWarnings("deprecation")
    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        boolean wasGliding = plugin.getPlayerDataManager().getInt(player.getUniqueId(), "soft_landing_gl", 0) == 1;
        boolean isGliding = player.isGliding();
        plugin.getPlayerDataManager().setInt(player.getUniqueId(), "soft_landing_gl", isGliding ? 1 : 0);
        if (wasGliding && !isGliding && !player.isOnGround()) {
            int duration = cfgi("duration", level) * 20;
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, duration, 0, true, false, true));
        }
    }

    @Override public String getDescription() { return "Slow Falling when you stop gliding."; }
    @Override public String getDescription(int level) {
        return "§7Grants §aSlow Falling§7 for §e" + level + "s§7 when glide ends mid-air."; }
}
