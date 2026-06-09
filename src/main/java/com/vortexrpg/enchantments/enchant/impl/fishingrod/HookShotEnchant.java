package com.vortexrpg.enchantments.enchant.impl.fishingrod;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;

import java.util.List;

/** HookShot: Rod deals direct damage when hooking entities. */
public class HookShotEnchant extends VortexEnchant {
    private static final double[] DMG = {2.0, 3.0, 4.0};

    public HookShotEnchant() { super("hook_shot", "Hook Shot", EnchantRarity.RARE, 3, List.of(ItemTarget.FISHING_ROD)); }

    @Override
    public void onFish(PlayerFishEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (event.getState() != PlayerFishEvent.State.CAUGHT_ENTITY) return;
        if (!(event.getCaught() instanceof LivingEntity target)) return;
        double damage = cfgd("damage", DMG[level - 1]);
        target.damage(damage, player);
    }

    @Override public String getDescription() { return "Deal damage on hooking entities."; }
    @Override public String getDescription(int level) {
        return "§7Hooking a mob deals §c" + (int) DMG[level - 1] + "§7 damage."; }
}
