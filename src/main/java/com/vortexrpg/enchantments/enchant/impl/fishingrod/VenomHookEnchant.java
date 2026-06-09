package com.vortexrpg.enchantments.enchant.impl.fishingrod;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/** VenomHook: Hooking an entity applies Poison. */
public class VenomHookEnchant extends VortexEnchant {

    public VenomHookEnchant() { super("venom_hook", "Venom Hook", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.FISHING_ROD)); }

    @Override
    public void onFish(PlayerFishEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (event.getState() != PlayerFishEvent.State.CAUGHT_ENTITY) return;
        if (!(event.getCaught() instanceof LivingEntity target)) return;
        int duration = cfgi("duration", 2 + level) * 20;
        int amp = cfgi("amplifier", Math.min(level - 1, 2));
        target.addPotionEffect(new PotionEffect(PotionEffectType.POISON, duration, amp, true, false));
    }

    @Override public String getDescription() { return "Hooked entities get poisoned."; }
    @Override public String getDescription(int level) {
        return "§7Hooking a mob applies §2Poison " + level + "§7 for §e" + (2 + level) + "s§7."; }
}
