package com.vortexrpg.enchantments.enchant.impl.chestplate;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/** Convert: On kill, heal 1/1.5/2 hearts. */
public class ConvertEnchant extends VortexEnchant {
    private static final double[] HEAL = {2.0, 3.0, 4.0};

    public ConvertEnchant() { super("convert", "Convert", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.CHESTPLATE)); }

    @Override
    public void onKill(EntityDamageByEntityEvent event, Player player, LivingEntity killed, int level) {
        if (!isEnabled()) return;
        double heal = cfg("heal", HEAL[level-1]);
        player.setHealth(Math.min(player.getHealth() + heal,
            player.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue()));
    }

    @Override public String getDescription() { return "Killing mobs heals you."; }
    @Override public String getDescription(int level) {
        return "§7On kill: §a+" + (HEAL[level-1]/2) + "§7 hearts healed."; }
}
