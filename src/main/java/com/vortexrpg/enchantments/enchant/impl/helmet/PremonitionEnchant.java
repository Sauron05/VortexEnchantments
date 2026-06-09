package com.vortexrpg.enchantments.enchant.impl.helmet;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

/** Premonition: Warns the player with a sound when a hostile mob targets them from behind. */
public class PremonitionEnchant extends VortexEnchant {
    public PremonitionEnchant() { super("premonition", "Premonition", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.HELMET)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        double radius = cfgd("radius", 6.0 + level * 2.0);
        org.bukkit.util.Vector look = player.getLocation().getDirection().normalize();
        for (LivingEntity e : player.getWorld().getNearbyLivingEntities(player.getLocation(), radius)) {
            if (!(e instanceof org.bukkit.entity.Monster)) continue;
            org.bukkit.util.Vector toEntity = e.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();
            if (look.dot(toEntity) < -0.3) {
                SoundUtil.play(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 0.3f, 2.0f);
                return;
            }
        }
    }

    @Override public String getDescription(int level) {
        return "§7Warns you when hostiles approach from §cbehind§7.";
    }
}
