package com.vortexrpg.enchantments.enchant.impl.boots;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;

import java.util.List;

/** Tremorsense: Feel nearby mob movement via actionbar (mob count + distance). */
public class TremorsenseEnchant extends VortexEnchant {
    public TremorsenseEnchant() { super("tremorsense", "Tremorsense", EnchantRarity.RARE, 1, List.of(ItemTarget.BOOTS)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        long tick = plugin.getServer().getCurrentTick();
        if (tick % 20 != 0) return;
        int r = cfgi("radius", 12);
        var nearby = player.getWorld().getNearbyLivingEntities(player.getLocation(), r, r, r,
            e -> !(e instanceof Player));
        if (nearby.isEmpty()) return;
        double minDist = nearby.stream().mapToDouble(e -> e.getLocation().distance(player.getLocation())).min().orElse(0);
        player.sendActionBar(net.kyori.adventure.text.Component.text(
            "§6[Tremorsense] §7" + nearby.size() + " mobs nearby, closest: §a" + (int)minDist + "§7 blocks"));
    }

    @Override public String getDescription() { return "Detects nearby mobs via actionbar."; }
    @Override public String getDescription(int level) { return "§7Sense nearby mob count + closest distance."; }
}
