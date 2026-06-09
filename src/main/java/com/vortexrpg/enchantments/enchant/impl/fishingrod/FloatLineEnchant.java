package com.vortexrpg.enchantments.enchant.impl.fishingrod;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;

import java.util.List;

/** FloatLine: Increases XP gained from fishing. */
public class FloatLineEnchant extends VortexEnchant {

    public FloatLineEnchant() { super("float_line", "Float Line", EnchantRarity.COMMON, 3, List.of(ItemTarget.FISHING_ROD)); }

    @Override
    public void onFish(PlayerFishEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;
        int bonusXp = cfgi("bonus_xp", level * 2);
        event.setExpToDrop(event.getExpToDrop() + bonusXp);
    }

    @Override public String getDescription() { return "Bonus XP from fishing."; }
    @Override public String getDescription(int level) {
        return "§7Fishing yields §a+" + (level * 2) + "§7 bonus XP per catch."; }
}
