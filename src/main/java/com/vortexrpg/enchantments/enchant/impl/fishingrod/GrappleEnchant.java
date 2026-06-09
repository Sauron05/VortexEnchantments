package com.vortexrpg.enchantments.enchant.impl.fishingrod;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.util.Vector;

import java.util.List;

/** Grapple: Hook blocks to pull yourself toward them. */
public class GrappleEnchant extends VortexEnchant {

    public GrappleEnchant() { super("grapple", "Grapple", EnchantRarity.RARE, 3, List.of(ItemTarget.FISHING_ROD)); }

    @Override
    public void onFish(PlayerFishEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (event.getState() != PlayerFishEvent.State.IN_GROUND) return;
        if (isOnCooldown(player)) return;
        setCooldownSeconds(player, cfgi("cooldown", Math.max(1, 4 - level)));
        Location hook = event.getHook().getLocation();
        double force = cfgd("force", 0.8 + level * 0.2);
        Vector dir = hook.toVector().subtract(player.getLocation().toVector()).normalize().multiply(force);
        dir.setY(Math.max(dir.getY(), 0.3));
        player.setVelocity(dir);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_FISHING_BOBBER_RETRIEVE, 1.0f, 1.5f);
    }

    @Override public String getDescription() { return "Hook terrain to grapple yourself."; }
    @Override public String getDescription(int level) {
        return "§7Hook blocks to §apull yourself§7 toward them (§e" + Math.max(1, 4 - level) + "s§7 cooldown)."; }
}
