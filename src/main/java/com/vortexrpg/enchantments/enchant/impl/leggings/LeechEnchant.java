package com.vortexrpg.enchantments.enchant.impl.leggings;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * Leech: HP steal on melee kill.
 */
public class LeechEnchant extends VortexEnchant {
    public LeechEnchant() { super("leech", "Leech", EnchantRarity.RARE, 3, List.of(ItemTarget.LEGGINGS)); }

    @Override
    public void onKill(EntityDamageByEntityEvent event, Player player, LivingEntity killed, int level) {
        if (!isEnabled()) return;
        double heal = cfgd("heal_on_kill", 2.0 * level);
        double maxHp = player.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue();
        player.setHealth(Math.min(maxHp, player.getHealth() + heal));
    }

    @Override public String getDescription(int level) {
        return "§7On kill: heal §a" + (2 * level) + " §7HP.";
    }
}
