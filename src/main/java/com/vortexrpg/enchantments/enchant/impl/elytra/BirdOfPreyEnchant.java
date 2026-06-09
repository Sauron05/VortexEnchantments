package com.vortexrpg.enchantments.enchant.impl.elytra;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/** BirdOfPrey: Strength buff for a few seconds after landing from a glide. */
public class BirdOfPreyEnchant extends VortexEnchant {

    public BirdOfPreyEnchant() { super("bird_of_prey", "Bird of Prey", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.ELYTRA)); }

    @SuppressWarnings("deprecation")
    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        boolean wasGliding = plugin.getPlayerDataManager().getInt(player.getUniqueId(), "bop_gl", 0) == 1;
        boolean isGliding = player.isGliding();
        plugin.getPlayerDataManager().setInt(player.getUniqueId(), "bop_gl", isGliding ? 1 : 0);
        if (wasGliding && !isGliding && player.isOnGround()) {
            int amp = Math.min(level - 1, 2);
            int duration = cfgi("duration", 3) * 20;
            player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, duration, amp, true, false, true));
        }
    }

    @Override public String getDescription() { return "Strength upon landing from flight."; }
    @Override public String getDescription(int level) {
        return "§7Grants §aStrength " + level + "§7 for §e3s§7 after landing from a glide."; }
}
