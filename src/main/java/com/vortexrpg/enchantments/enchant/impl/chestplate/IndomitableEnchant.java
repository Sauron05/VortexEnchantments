package com.vortexrpg.enchantments.enchant.impl.chestplate;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * Indomitable: Max damage per single hit is capped. Cannot be one-shot.
 */
public class IndomitableEnchant extends VortexEnchant {
    public IndomitableEnchant() { super("indomitable", "Indomitable", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.CHESTPLATE)); }

    @Override
    public void onDamaged(EntityDamageByEntityEvent event, Player victim, Entity attacker, int level) {
        if (!isEnabled()) return;
        double maxHp = victim.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue();
        double cap = cfgd("damage_cap_pct", 0.60 - level * 0.10);
        double maxDmg = maxHp * cap;
        if (event.getDamage() > maxDmg) {
            event.setDamage(maxDmg);
        }
    }

    @Override public String getDescription(int level) {
        return "§7Max damage per hit capped at §a" + (int)((60 - level * 10)) + "% §7of max HP.";
    }
}
