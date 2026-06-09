package com.vortexrpg.enchantments.enchant.impl.elytra;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;

/** AeroLift: Periodic small upward velocity boost while gliding. */
public class AeroLiftEnchant extends VortexEnchant {

    public AeroLiftEnchant() { super("aero_lift", "Aero Lift", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.ELYTRA)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled() || !player.isGliding()) return;
        int interval = cfgi("interval", Math.max(1, 6 - level));
        int ticks = plugin.getPlayerDataManager().getInt(player.getUniqueId(), "aero_lift_t", 0) + 1;
        if (ticks >= interval) {
            ticks = 0;
            double lift = cfgd("lift", 0.10 + level * 0.05);
            player.setVelocity(player.getVelocity().add(new Vector(0, lift, 0)));
        }
        plugin.getPlayerDataManager().setInt(player.getUniqueId(), "aero_lift_t", ticks);
    }

    @Override public String getDescription() { return "Periodic upward lift while gliding."; }
    @Override public String getDescription(int level) {
        return "§7Small upward boost every §e" + Math.max(1, 6 - level) + "s§7 while gliding."; }
}
