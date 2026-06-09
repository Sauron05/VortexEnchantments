package com.vortexrpg.enchantments.enchant.impl.fishingrod;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.util.Vector;

import java.util.List;

/** TidePull: Hooked entities get pulled with extra force. */
public class TidePullEnchant extends VortexEnchant {

    public TidePullEnchant() { super("tide_pull", "Tide Pull", EnchantRarity.RARE, 3, List.of(ItemTarget.FISHING_ROD)); }

    @Override
    public void onFish(PlayerFishEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (event.getState() != PlayerFishEvent.State.CAUGHT_ENTITY) return;
        if (!(event.getCaught() instanceof LivingEntity target)) return;
        double force = cfgd("force", 1.0 + level * 0.5);
        Vector pull = player.getLocation().toVector().subtract(target.getLocation().toVector()).normalize().multiply(force);
        pull.setY(Math.max(pull.getY(), 0.3));
        target.setVelocity(pull);
    }

    @Override public String getDescription() { return "Hooked entities get yanked harder."; }
    @Override public String getDescription(int level) {
        return "§7Hooked mobs are pulled with §a" + String.format("%.0f%%", (1.0 + level * 0.5) * 100) + "§7 force."; }
}
