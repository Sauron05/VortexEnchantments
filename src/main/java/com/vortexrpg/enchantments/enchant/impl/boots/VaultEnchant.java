package com.vortexrpg.enchantments.enchant.impl.boots;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.util.List;

/** Vault: Double-jump (sneak mid-air) launches you upward. 8s cooldown. */
@SuppressWarnings("deprecation")
public class VaultEnchant extends VortexEnchant {
    private static final double[] POWER = {0.6, 0.8, 1.0};

    public VaultEnchant() { super("vault", "Vault", EnchantRarity.RARE, 3, List.of(ItemTarget.BOOTS)); }

    @Override
    public void onToggleSneak(PlayerToggleSneakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!event.isSneaking()) return;
        if (player.isOnGround()) return;
        if (isOnCooldown(player)) return;
        setCooldownSeconds(player, 8);
        double power = cfg("power", POWER[level-1]);
        player.setVelocity(player.getVelocity().setY(power));
        com.vortexrpg.enchantments.util.ParticleUtil.burst(player.getLocation(), org.bukkit.Particle.CLOUD, 10, 0.4f);
    }

    @Override public String getDescription() { return "Sneak mid-air to vault upward."; }
    @Override public String getDescription(int level) {
        return "§7Sneak in air: launch §a" + POWER[level-1] + "§7 blocks up (§a8s§7 cd)."; }
}
