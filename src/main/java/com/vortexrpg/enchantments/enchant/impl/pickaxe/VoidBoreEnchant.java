package com.vortexrpg.enchantments.enchant.impl.pickaxe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/** Void Bore: Below Y=-32 grants Haste III for rapid mining. */
public class VoidBoreEnchant extends VortexEnchant {
    public VoidBoreEnchant() { super("void_bore", "Void Bore", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.PICKAXE)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        if (player.getLocation().getBlockY() < cfgi("threshold_y", -32)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, 40, level, true, false));
        }
    }

    @Override public String getDescription() { return "Extreme mining speed deep underground."; }
    @Override public String getDescription(int level) {
        return "§7Below Y=-32: §aHaste " + (level + 1) + "§7."; }
}
