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

/** LeviathansGrasp: Hooking a mob roots them in place. */
public class LeviathansGraspEnchant extends VortexEnchant {

    public LeviathansGraspEnchant() { super("leviathans_grasp", "Leviathan's Grasp", EnchantRarity.EPIC, 3, List.of(ItemTarget.FISHING_ROD)); }

    @Override
    public void onFish(PlayerFishEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (event.getState() != PlayerFishEvent.State.CAUGHT_ENTITY) return;
        if (!(event.getCaught() instanceof LivingEntity target)) return;
        int duration = cfgi("root_duration", 1 + level) * 20;
        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, duration, 127, true, false));
        target.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, duration, 128, true, false));
    }

    @Override public String getDescription() { return "Hooked mobs are rooted in place."; }
    @Override public String getDescription(int level) {
        return "§7Hooking a mob §croots§7 them for §e" + (1 + level) + "s§7 (no movement)."; }
}
