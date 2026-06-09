package com.vortexrpg.enchantments.enchant.impl.leggings;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/** Tangle: Attacks have 15/20/30% to apply Slowness II to target for 2/3/4s. */
public class TangleEnchant extends VortexEnchant {
    private static final double[] CHANCE = {0.15, 0.20, 0.30};
    private static final int[] DUR = {2, 3, 4};

    public TangleEnchant() { super("tangle", "Tangle", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.LEGGINGS)); }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player player, LivingEntity target, int level) {
        if (!isEnabled()) return;
        double chance = cfg("chance", CHANCE[level-1]);
        if (Math.random() < chance) {
            int dur = cfgi("duration", DUR[level-1]);
            target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20 * dur, 1));
        }
    }

    @Override public String getDescription() { return "Attacks may root enemies in place."; }
    @Override public String getDescription(int level) {
        return "§a" + (int)(CHANCE[level-1]*100) + "§a%§7 to apply §aSlowness II§7 for §a" + DUR[level-1] + "s§7."; }
}
