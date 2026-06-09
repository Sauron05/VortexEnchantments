package com.vortexrpg.enchantments.enchant.impl.elytra;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/** EagleEye: Night Vision while gliding. */
public class EagleEyeEnchant extends VortexEnchant {

    public EagleEyeEnchant() { super("eagle_eye", "Eagle Eye", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.ELYTRA)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled() || !player.isGliding()) return;
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 60, 0, true, false, true));
    }

    @Override public String getDescription() { return "See clearly while gliding."; }
    @Override public String getDescription(int level) {
        return "§7Grants §aNight Vision§7 while gliding."; }
}
