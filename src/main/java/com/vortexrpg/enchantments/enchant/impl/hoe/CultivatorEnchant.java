package com.vortexrpg.enchantments.enchant.impl.hoe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/** Cultivator: Tilling soil grants brief Luck effect. */
public class CultivatorEnchant extends VortexEnchant {

    public CultivatorEnchant() { super("cultivator", "Cultivator", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.HOE)); }

    @Override
    public void onInteract(PlayerInteractEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!event.getAction().isRightClick()) return;
        if (event.getClickedBlock() == null) return;
        Material mat = event.getClickedBlock().getType();
        if (mat != Material.DIRT && mat != Material.GRASS_BLOCK) return;
        int duration = cfgi("duration", 60 + level * 40);
        player.addPotionEffect(new PotionEffect(PotionEffectType.LUCK, duration, level - 1, true, false));
    }

    @Override public String getDescription() { return "Tilling grants Luck effect."; }
    @Override public String getDescription(int level) {
        return "§7Till soil: §aLuck " + level + "§7 for §e" + ((60 + level * 40) / 20) + "s§7."; }
}
