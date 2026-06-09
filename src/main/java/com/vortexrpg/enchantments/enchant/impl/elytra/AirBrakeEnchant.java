package com.vortexrpg.enchantments.enchant.impl.elytra;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.util.List;

/** AirBrake: Sneaking while gliding decelerates quickly. */
public class AirBrakeEnchant extends VortexEnchant {

    public AirBrakeEnchant() { super("air_brake", "Air Brake", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.ELYTRA)); }

    @Override
    public void onToggleSneak(PlayerToggleSneakEvent event, Player player, int level) {
        if (!isEnabled() || !player.isGliding() || !event.isSneaking()) return;
        double factor = cfgd("brake_factor", 0.3 + level * 0.1);
        player.setVelocity(player.getVelocity().multiply(1.0 - factor));
    }

    @Override public String getDescription() { return "Sneak to brake mid-flight."; }
    @Override public String getDescription(int level) {
        return "§7Sneaking while gliding reduces speed by §a" + (int)((0.3 + level * 0.1) * 100) + "%§7."; }
}
