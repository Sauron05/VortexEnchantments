package com.vortexrpg.enchantments.enchant.impl.leggings;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/** Tithe: 20/25/30% of all damage dealt heals you. */
public class TitheEnchant extends VortexEnchant {
    private static final double[] RATIO = {0.20, 0.25, 0.30};

    public TitheEnchant() { super("tithe", "Tithe", EnchantRarity.RARE, 3, List.of(ItemTarget.LEGGINGS)); }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player player, org.bukkit.entity.LivingEntity target, int level) {
        if (!isEnabled()) return;
        double ratio = cfg("ratio", RATIO[level-1]);
        double heal = event.getFinalDamage() * ratio;
        double maxHp = player.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue();
        player.setHealth(Math.min(player.getHealth() + heal, maxHp));
    }

    @Override public String getDescription() { return "Life steal on attack."; }
    @Override public String getDescription(int level) {
        return "§7Restore §a" + (int)(RATIO[level-1]*100) + "§a%§7 of damage dealt as health."; }
}
