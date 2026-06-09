package com.vortexrpg.enchantments.enchant.impl.helmet;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/** PressureSeal: Reduce damage while below 50% health by 5/10/15%. */
public class PressureSealEnchant extends VortexEnchant {
    private static final double[] REDUCE = {0.05, 0.10, 0.15};

    public PressureSealEnchant() { super("pressure_seal", "Pressure Seal", EnchantRarity.RARE, 3, List.of(ItemTarget.HELMET)); }

    @Override
    public void onDamageTaken(EntityDamageByEntityEvent event, Player player, int level) {
        if (!isEnabled()) return;
        double maxHp = player.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue();
        if (player.getHealth() > maxHp * 0.5) return;
        double reduce = cfg("reduce", REDUCE[level-1]);
        event.setDamage(event.getDamage() * (1.0 - reduce));
    }

    @Override public String getDescription() { return "Reduces damage when below half health."; }
    @Override public String getDescription(int level) {
        return "§7Below 50% HP: §a-" + (int)(REDUCE[level-1]*100) + "§a%§7 damage."; }
}
