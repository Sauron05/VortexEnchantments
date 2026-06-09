package com.vortexrpg.enchantments.enchant.impl.fishingrod;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/** TidalLure: Speed buff while fishing in ocean or river biomes. */
public class TidalLureEnchant extends VortexEnchant {

    public TidalLureEnchant() { super("tidal_lure", "Tidal Lure", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.FISHING_ROD)); }

    @Override
    public void onFish(PlayerFishEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (event.getState() != PlayerFishEvent.State.FISHING) return;
        String biome = player.getLocation().getBlock().getBiome().getKey().value();
        if (biome.contains("ocean") || biome.contains("river") || biome.contains("beach")) {
            int duration = cfgi("duration", 10 + level * 5) * 20;
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration, 0, true, false, true));
        }
    }

    @Override public String getDescription() { return "Speed buff when fishing in water biomes."; }
    @Override public String getDescription(int level) {
        return "§7Fishing in ocean/river grants §aSpeed I§7 for §e" + (10 + level * 5) + "s§7."; }
}
