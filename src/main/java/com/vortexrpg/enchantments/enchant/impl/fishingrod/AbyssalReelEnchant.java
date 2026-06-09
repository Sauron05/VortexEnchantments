package com.vortexrpg.enchantments.enchant.impl.fishingrod;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.List;

/** AbyssalReel: Hook entities and drag them down. */
public class AbyssalReelEnchant extends VortexEnchant {

    public AbyssalReelEnchant() { super("abyssal_reel", "Abyssal Reel", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.FISHING_ROD)); }

    @Override
    public void onFish(PlayerFishEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (event.getState() != PlayerFishEvent.State.CAUGHT_ENTITY) return;
        if (!(event.getCaught() instanceof LivingEntity target)) return;
        double pullDown = cfgd("pull_down", 0.3 + level * 0.2);
        target.setVelocity(new Vector(0, -pullDown, 0));
        int duration = cfgi("slow_duration", 2 + level) * 20;
        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, duration, 4, true, false));
        target.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, duration, 0, true, false));
    }

    @Override public String getDescription() { return "Drag hooked entities into the depths."; }
    @Override public String getDescription(int level) {
        return "§7Hooked mobs are §5dragged down§7 + §cSlowness V + Darkness§7 for §e" + (2 + level) + "s§7."; }
}
