package com.vortexrpg.enchantments.enchant.impl.fishingrod;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.util.Vector;

import java.util.List;

/** RiptideRod: Catching pulls you slightly toward the bobber. */
public class RiptideRodEnchant extends VortexEnchant {

    public RiptideRodEnchant() { super("riptide_rod", "Riptide Rod", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.FISHING_ROD)); }

    @Override
    public void onFish(PlayerFishEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;
        if (event.getHook() == null) return;
        double force = cfgd("pull_force", 0.3 + level * 0.15);
        Vector dir = event.getHook().getLocation().toVector().subtract(player.getLocation().toVector()).normalize().multiply(force);
        dir.setY(Math.max(dir.getY(), 0.2));
        player.setVelocity(player.getVelocity().add(dir));
    }

    @Override public String getDescription() { return "Get pulled toward your catch."; }
    @Override public String getDescription(int level) {
        return "§7On catch, pull yourself toward the bobber with §a" + String.format("%.0f%%", (0.3 + level * 0.15) * 100) + "§7 force."; }
}
