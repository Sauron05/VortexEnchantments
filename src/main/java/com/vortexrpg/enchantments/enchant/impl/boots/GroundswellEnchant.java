package com.vortexrpg.enchantments.enchant.impl.boots;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.List;

/** Groundswell: Crossing 50/40/30 different block types triggers a brief Strength and Speed burst. */
public class GroundswellEnchant extends VortexEnchant {
    private static final int[] THRESHOLD = {50, 40, 30};

    public GroundswellEnchant() { super("groundswell", "Groundswell", EnchantRarity.RARE, 3, List.of(ItemTarget.BOOTS)); }

    @Override
    public void onMove(PlayerMoveEvent event, Player player, int level) {
        if (!isEnabled() || !event.hasChangedBlock()) return;
        var blockType = player.getLocation().getBlock().getRelative(0, -1, 0).getType().name();
        String key = "groundswell_block_" + blockType.hashCode();
        if (plugin.getPlayerDataManager().getInt(player.getUniqueId(), key) == 0) {
            plugin.getPlayerDataManager().setInt(player.getUniqueId(), key, 1);
            int count = plugin.getPlayerDataManager().getInt(player.getUniqueId(), "groundswell_count") + 1;
            int threshold = cfgi("threshold", THRESHOLD[level-1]);
            if (count >= threshold) {
                plugin.getPlayerDataManager().setInt(player.getUniqueId(), "groundswell_count", 0);
                player.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.STRENGTH, 100, 0));
                player.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.SPEED, 100, 1));
                player.sendActionBar(net.kyori.adventure.text.Component.text("§2[Groundswell] §7Burst activated!"));
            } else {
                plugin.getPlayerDataManager().setInt(player.getUniqueId(), "groundswell_count", count);
            }
        }
    }

    @Override public String getDescription() { return "Stepping on unique blocks charges a strength burst."; }
    @Override public String getDescription(int level) {
        return "§7Step on §a" + THRESHOLD[level-1] + "§7 unique blocks: §aStrength I§7 + §aSpeed II§7."; }
}
