package com.vortexrpg.enchantments.enchant.impl.chestplate;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/** Adrenaline: On kill, gain Speed II + Strength I for 4/6/8 seconds. */
public class AdrenalineEnchant extends VortexEnchant {
    private static final int[] DUR = {4, 6, 8};

    public AdrenalineEnchant() { super("adrenaline", "Adrenaline", EnchantRarity.RARE, 3, List.of(ItemTarget.CHESTPLATE)); }

    @Override
    public void onKill(EntityDamageByEntityEvent event, Player player, LivingEntity killed, int level) {
        if (!isEnabled()) return;
        int dur = cfgi("duration", DUR[level-1]);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * dur, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 20 * dur, 0));
    }

    @Override public String getDescription() { return "Killing grants a burst of Speed and Strength."; }
    @Override public String getDescription(int level) {
        return "§7On kill: §aSpeed II§7 + §aStrength I§7 for §a" + DUR[level-1] + "s§7."; }
}
