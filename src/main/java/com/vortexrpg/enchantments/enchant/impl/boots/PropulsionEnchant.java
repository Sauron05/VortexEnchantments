package com.vortexrpg.enchantments.enchant.impl.boots;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;

/** Propulsion: Sneak + jump launches you forward 3/5/7 blocks. 5s cooldown. */
@SuppressWarnings("deprecation")
public class PropulsionEnchant extends VortexEnchant {
    private static final double[] POWER = {3.0, 5.0, 7.0};

    public PropulsionEnchant() { super("propulsion", "Propulsion", EnchantRarity.RARE, 3, List.of(ItemTarget.BOOTS)); }

    @Override
    public void onToggleSneak(org.bukkit.event.player.PlayerToggleSneakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!event.isSneaking()) return;
        if (isOnCooldown(player)) return;
        if (!player.isOnGround()) return;
        setCooldownSeconds(player, 5);
        double power = cfg("power", POWER[level-1]) / 10.0;
        Vector dir = player.getLocation().getDirection().setY(0.4).normalize().multiply(power);
        player.setVelocity(dir);
        com.vortexrpg.enchantments.util.ParticleUtil.trail(player.getLocation(), org.bukkit.Particle.CLOUD, 5, 0.3f);
    }

    @Override public String getDescription() { return "Sneak while grounded to propel yourself forward."; }
    @Override public String getDescription(int level) {
        return "§7Sneak (ground): launch §a" + POWER[level-1] + "§7 blocks forward (§a5s§7 cd)."; }
}
