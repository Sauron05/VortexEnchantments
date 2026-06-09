package com.vortexrpg.enchantments.enchant.impl.shovel;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/** Layline: Every N blocks mined in a line grants Speed buff. */
public class LaylineEnchant extends VortexEnchant {

    public LaylineEnchant() { super("layline", "Layline", EnchantRarity.EPIC, 3, List.of(ItemTarget.SHOVEL)); }

    @Override
    public void onBlockBreak(BlockBreakEvent event, Player player, int level) {
        if (!isEnabled()) return;
        String key = "layline_count";
        int count = plugin.getPlayerDataManager().getInt(player.getUniqueId(), key) + 1;
        int threshold = cfgi("threshold", 10);
        if (count >= threshold) {
            int duration = cfgi("speed-duration", 60 + level * 40);
            int amplifier = level - 1;
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration, amplifier));
            ParticleUtil.burst(player.getLocation().add(0, 1, 0), Particle.HAPPY_VILLAGER, 15, 1.0);
            plugin.getPlayerDataManager().setInt(player.getUniqueId(), key, 0);
        } else {
            plugin.getPlayerDataManager().setInt(player.getUniqueId(), key, count);
        }
    }

    @Override public String getDescription() { return "Every 10 blocks mined grants Speed."; }
    @Override public String getDescription(int level) {
        return "§7Every §e10§7 blocks: §bSpeed " + level + "§7 for " + (3 + level * 2) + "s."; }
}
