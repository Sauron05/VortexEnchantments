package com.vortexrpg.enchantments.enchant.impl.chestplate;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/** Reciprocity: You deal extra damage proportional to your missing HP (up to +20/30/40%). */
public class ReciprocityEnchant extends VortexEnchant {
    private static final double[] MAX_BONUS = {0.20, 0.30, 0.40};

    public ReciprocityEnchant() { super("reciprocity", "Reciprocity", EnchantRarity.EPIC, 3, List.of(ItemTarget.CHESTPLATE)); }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player player, org.bukkit.entity.LivingEntity target, int level) {
        if (!isEnabled()) return;
        double maxHp = player.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue();
        double missingFraction = (maxHp - player.getHealth()) / maxHp;
        double maxBonus = cfg("max_bonus", MAX_BONUS[level-1]);
        double bonus = missingFraction * maxBonus;
        event.setDamage(event.getDamage() * (1.0 + bonus));
    }

    @Override public String getDescription() { return "Your pain powers your strikes."; }
    @Override public String getDescription(int level) {
        return "§7Damage bonus scales with missing HP (max §a+" + (int)(MAX_BONUS[level-1]*100) + "§a%§7)."; }
}
