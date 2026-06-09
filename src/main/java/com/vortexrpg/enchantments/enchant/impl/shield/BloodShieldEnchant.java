package com.vortexrpg.enchantments.enchant.impl.shield;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/** Blood Shield: Blocked damage heals you for a small percentage. */
public class BloodShieldEnchant extends VortexEnchant {

    public BloodShieldEnchant() { super("blood_shield", "Blood Shield", EnchantRarity.RARE, 3, List.of(ItemTarget.SHIELD)); }

    @Override
    public void onDamageTaken(EntityDamageByEntityEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!player.isBlocking()) return;
        double percent = cfg("heal-percent", 3.0 + level * 2);
        double heal = event.getDamage() * (percent / 100.0);
        double maxHp = player.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue();
        player.setHealth(Math.min(player.getHealth() + heal, maxHp));
    }

    @Override public String getDescription() { return "Blocked damage heals you."; }
    @Override public String getDescription(int level) {
        return "§7Block: heal §a" + (int)(3 + level * 2) + "%§7 of blocked damage."; }
}
