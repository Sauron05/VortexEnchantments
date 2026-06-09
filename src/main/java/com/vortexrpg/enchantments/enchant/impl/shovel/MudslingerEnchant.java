package com.vortexrpg.enchantments.enchant.impl.shovel;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

/** Mudslinger: Right-click to throw a dirt projectile — blinds target for 1/1.5/2 seconds. */
public class MudslingerEnchant extends VortexEnchant {
    private static final double[] BLIND_DUR = {1.0, 1.5, 2.0};

    public MudslingerEnchant() { super("mudslinger", "Mudslinger", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.SHOVEL)); }

    @Override
    public void onInteract(PlayerInteractEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (isOnCooldown(player)) return;
        // Check player has dirt
        if (!player.getInventory().contains(Material.DIRT)) return;
        player.getInventory().removeItem(new org.bukkit.inventory.ItemStack(Material.DIRT, 1));
        setCooldownSeconds(player, cfgi("cooldown", 5));
        Snowball mud = player.launchProjectile(Snowball.class);
        mud.setVelocity(player.getEyeLocation().getDirection().multiply(1.5));
        int blindTicks = (int)(cfg("blind_duration_" + level, BLIND_DUR[level-1]) * 20);
        mud.setMetadata("mudslinger_blind", new org.bukkit.metadata.FixedMetadataValue(plugin, blindTicks));
        SoundUtil.play(player.getLocation(), Sound.BLOCK_GRAVEL_BREAK, 1f, 0.8f);
    }

    @Override public String getDescription() { return "Throw a dirt ball to blind enemies."; }
    @Override public String getDescription(int level) {
        return "§7Right-click (costs 1 dirt): throw §6mud§7 that blinds for §e" + BLIND_DUR[level-1] + "s§7."; }
}
