package com.vortexrpg.enchantments.enchant.impl.helmet;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;

import java.util.List;

/** Sonar: Highlights (glows) nearby entities visible in action bar; higher = longer range. */
public class SonarEnchant extends VortexEnchant {
    private static final int[] RADIUS = {8, 12, 18};

    public SonarEnchant() { super("sonar", "Sonar", EnchantRarity.RARE, 3, List.of(ItemTarget.HELMET)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        long tick = plugin.getServer().getCurrentTick();
        if (tick % 40 != 0) return;
        int r = cfgi("radius", RADIUS[level-1]);
        player.getWorld().getNearbyLivingEntities(player.getLocation(), r, r, r,
            e -> !(e instanceof Player)).forEach(e -> e.setGlowing(true));
    }

    @Override public String getDescription() { return "Nearby mobs glow through walls."; }
    @Override public String getDescription(int level) {
        return "§7Mobs within §a" + RADIUS[level-1] + "§7 blocks glow."; }
}
