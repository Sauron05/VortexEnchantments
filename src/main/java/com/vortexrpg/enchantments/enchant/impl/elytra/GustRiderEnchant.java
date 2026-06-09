package com.vortexrpg.enchantments.enchant.impl.elytra;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;

/** GustRider: Massive initial speed burst when starting to glide. */
public class GustRiderEnchant extends VortexEnchant {

    public GustRiderEnchant() { super("gust_rider", "Gust Rider", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.ELYTRA)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        boolean wasGliding = plugin.getPlayerDataManager().getInt(player.getUniqueId(), "gust_gl", 0) == 1;
        boolean isGliding = player.isGliding();
        plugin.getPlayerDataManager().setInt(player.getUniqueId(), "gust_gl", isGliding ? 1 : 0);
        if (!wasGliding && isGliding) {
            double burst = cfgd("burst", 0.5 + level * 0.25);
            Vector dir = player.getLocation().getDirection().multiply(burst);
            player.setVelocity(player.getVelocity().add(dir));
        }
    }

    @Override public String getDescription() { return "Powerful launch boost when starting to glide."; }
    @Override public String getDescription(int level) {
        return "§7Big §a+" + (int)((0.5 + level * 0.25) * 100) + "%§7 speed burst when glide starts."; }
}
