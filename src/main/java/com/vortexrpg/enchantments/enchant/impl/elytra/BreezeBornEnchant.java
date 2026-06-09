package com.vortexrpg.enchantments.enchant.impl.elytra;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;

/** BreezeBorn: Small forward velocity boost while gliding. */
public class BreezeBornEnchant extends VortexEnchant {

    public BreezeBornEnchant() { super("breeze_born", "Breeze Born", EnchantRarity.COMMON, 3, List.of(ItemTarget.ELYTRA)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled() || !player.isGliding()) return;
        double boost = cfgd("boost", 0.02 * level);
        Vector dir = player.getLocation().getDirection().multiply(boost);
        player.setVelocity(player.getVelocity().add(dir));
    }

    @Override public String getDescription() { return "Gentle speed boost while gliding."; }
    @Override public String getDescription(int level) {
        return "§7Adds a small §a+" + (int)(level * 2) + "%§7 forward boost while gliding."; }
}
