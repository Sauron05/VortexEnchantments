package com.vortexrpg.enchantments.enchant.impl.fishingrod;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;

import java.util.List;

/** SirenSong: Bobber increases next catch XP significantly. */
public class SirenSongEnchant extends VortexEnchant {

    public SirenSongEnchant() { super("siren_song", "Siren Song", EnchantRarity.RARE, 3, List.of(ItemTarget.FISHING_ROD)); }

    @Override
    public void onFish(PlayerFishEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
            int multiplier = cfgi("xp_multiplier", 1 + level);
            event.setExpToDrop(event.getExpToDrop() * multiplier);
        }
    }

    @Override public String getDescription() { return "Multiplied XP from fishing."; }
    @Override public String getDescription(int level) {
        return "§7Catches yield §a" + (1 + level) + "x§7 XP."; }
}
