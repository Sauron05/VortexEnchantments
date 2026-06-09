package com.vortexrpg.enchantments.enchant.impl.elytra;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/** Slipstream: Speed II for 3/4/5s when starting to glide. */
public class SlipstreamEnchant extends VortexEnchant {

    public SlipstreamEnchant() { super("slipstream", "Slipstream", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.ELYTRA)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        boolean wasGliding = plugin.getPlayerDataManager().getInt(player.getUniqueId(), "slipstream_gl", 0) == 1;
        boolean isGliding = player.isGliding();
        plugin.getPlayerDataManager().setInt(player.getUniqueId(), "slipstream_gl", isGliding ? 1 : 0);
        if (!wasGliding && isGliding) {
            int duration = cfgi("duration", 2 + level) * 20;
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration, 1, true, false, true));
        }
    }

    @Override public String getDescription() { return "Burst of speed when starting to glide."; }
    @Override public String getDescription(int level) {
        return "§7Grants §aSpeed II§7 for §e" + (2 + level) + "s§7 when glide begins."; }
}
