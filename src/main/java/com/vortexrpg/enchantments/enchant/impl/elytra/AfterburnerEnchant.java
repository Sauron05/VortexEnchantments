package com.vortexrpg.enchantments.enchant.impl.elytra;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;

/** Afterburner: Each consecutive second of gliding stacks speed. */
public class AfterburnerEnchant extends VortexEnchant {

    public AfterburnerEnchant() { super("afterburner", "Afterburner", EnchantRarity.RARE, 3, List.of(ItemTarget.ELYTRA)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        if (player.isGliding()) {
            int stacks = plugin.getPlayerDataManager().getInt(player.getUniqueId(), "afterburner_s", 0);
            int maxStacks = cfgi("max_stacks", 10);
            if (stacks < maxStacks) {
                stacks++;
                plugin.getPlayerDataManager().setInt(player.getUniqueId(), "afterburner_s", stacks);
            }
            double boostPer = cfgd("boost_per_stack", 0.01 + level * 0.005);
            Vector dir = player.getLocation().getDirection().multiply(boostPer * stacks);
            player.setVelocity(player.getVelocity().add(dir));
        } else {
            plugin.getPlayerDataManager().setInt(player.getUniqueId(), "afterburner_s", 0);
        }
    }

    @Override public String getDescription() { return "Speed increases the longer you glide."; }
    @Override public String getDescription(int level) {
        return "§7Each second of glide adds §a+" + (int)((0.01 + level * 0.005) * 100) + "%§7 speed (max 10 stacks)."; }
}
