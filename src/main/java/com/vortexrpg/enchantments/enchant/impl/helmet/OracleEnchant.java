package com.vortexrpg.enchantments.enchant.impl.helmet;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;

import java.util.List;

/** Oracle: Displays nearby entity count and health sum in action bar each second. */
public class OracleEnchant extends VortexEnchant {
    public OracleEnchant() { super("oracle", "Oracle", EnchantRarity.EPIC, 1, List.of(ItemTarget.HELMET)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        long tick = plugin.getServer().getCurrentTick();
        if (tick % 20 != 0) return;
        int r = cfgi("radius", 20);
        var nearby = player.getWorld().getNearbyLivingEntities(player.getLocation(), r, r, r,
            e -> !(e instanceof Player));
        if (nearby.isEmpty()) return;
        double totalHp = nearby.stream().mapToDouble(e -> e.getHealth()).sum();
        player.sendActionBar(net.kyori.adventure.text.Component.text(
            "§6[Oracle] §7Nearby: §a" + nearby.size() + "§7 mobs | Total HP: §c" + (int)totalHp));
    }

    @Override public String getDescription() { return "See nearby mob count and HP."; }
    @Override public String getDescription(int level) { return "§7Action bar shows nearby mob count and total HP."; }
}
