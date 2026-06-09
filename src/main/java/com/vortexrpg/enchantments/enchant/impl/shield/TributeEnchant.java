package com.vortexrpg.enchantments.enchant.impl.shield;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/** Tribute: When blocking, 10/15/20% of blocked damage heals you. */
public class TributeEnchant extends VortexEnchant {
    private static final double[] RATIO = {0.10, 0.15, 0.20};

    public TributeEnchant() { super("tribute", "Tribute", EnchantRarity.RARE, 3, List.of(ItemTarget.SHIELD)); }

    @Override
    public void onDamageTaken(EntityDamageByEntityEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!player.isBlocking()) return;
        double ratio = cfg("ratio", RATIO[level-1]);
        double heal = event.getDamage() * ratio;
        double maxHp = player.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue();
        player.setHealth(Math.min(player.getHealth() + heal, maxHp));
    }

    @Override public String getDescription() { return "Blocking heals you."; }
    @Override public String getDescription(int level) {
        return "§7Blocking converts §a" + (int)(RATIO[level-1]*100) + "§a%§7 of damage to healing."; }
}
