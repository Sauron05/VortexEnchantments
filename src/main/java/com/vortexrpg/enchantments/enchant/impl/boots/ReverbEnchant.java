package com.vortexrpg.enchantments.enchant.impl.boots;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.List;

/** Reverb: Each block moved charges a counter; at 20 blocks, emit an area Slowness pulse. */
public class ReverbEnchant extends VortexEnchant {
    private static final int THRESHOLD = 20;

    public ReverbEnchant() { super("reverb", "Reverb", EnchantRarity.RARE, 3, List.of(ItemTarget.BOOTS)); }

    @Override
    public void onMove(PlayerMoveEvent event, Player player, int level) {
        if (!isEnabled() || !event.hasChangedBlock()) return;
        int count = plugin.getPlayerDataManager().getInt(player.getUniqueId(), "reverb_count") + 1;
        int threshold = cfgi("threshold", THRESHOLD);
        if (count >= threshold) {
            count = 0;
            int r = cfgi("radius", 4 + level);
            int dur = cfgi("duration", level);
            player.getWorld().getNearbyLivingEntities(player.getLocation(), r, r, r,
                e -> !(e instanceof Player)).forEach(e ->
                e.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.SLOWNESS, 20 * dur, 0)));
            com.vortexrpg.enchantments.util.ParticleUtil.ring(player.getLocation(), org.bukkit.Particle.SONIC_BOOM, 10, r);
        }
        plugin.getPlayerDataManager().setInt(player.getUniqueId(), "reverb_count", count);
    }

    @Override public String getDescription() { return "Moving builds up a Slowness pulse."; }
    @Override public String getDescription(int level) {
        return "§7Every §a20§7 blocks: slow nearby mobs for §a" + level + "s§7."; }
}
