package com.vortexrpg.enchantments.enchant.impl.helmet;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;

import java.util.List;

/** Babel: Translates mob sounds into player chat hints about nearby mob types. */
public class BabelEnchant extends VortexEnchant {
    public BabelEnchant() { super("babel", "Babel", EnchantRarity.UNCOMMON, 1, List.of(ItemTarget.HELMET)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        // Every ~5 seconds, list nearby mob types
        long tick = plugin.getServer().getCurrentTick();
        if (tick % 100 != 0) return;
        long r = cfgi("radius", 16);
        var nearby = player.getWorld().getNearbyLivingEntities(player.getLocation(), r, r, r,
            e -> !(e instanceof Player));
        if (nearby.isEmpty()) return;
        var counts = new java.util.HashMap<String, Integer>();
        for (var e : nearby) counts.merge(e.getType().name().replace("_", " ").toLowerCase(), 1, (a, b) -> a + b);
        var sb = new StringBuilder("§e[Babel] Near: ");
        counts.forEach((t, c) -> sb.append(c).append("× ").append(t).append(" "));
        player.sendActionBar(net.kyori.adventure.text.Component.text(sb.toString().trim()));
    }

    @Override public String getDescription() { return "Sense nearby mobs via actionbar."; }
    @Override public String getDescription(int level) { return "§7Nearby mob types shown in action bar."; }
}
