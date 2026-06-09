package com.vortexrpg.enchantments.enchant.impl.chestplate;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/** ScarTissue: Accumulated damage builds DR (1 DR per 10 damage, up to 5/10/15%). */
public class ScarTissueEnchant extends VortexEnchant {
    public ScarTissueEnchant() { super("scar_tissue", "Scar Tissue", EnchantRarity.RARE, 3, List.of(ItemTarget.CHESTPLATE)); }

    @Override
    public void onDamageTaken(EntityDamageByEntityEvent event, Player player, int level) {
        if (!isEnabled()) return;
        double add = event.getFinalDamage();
        plugin.getPlayerDataManager().addScarResist(player.getUniqueId(), add);
        double resist = plugin.getPlayerDataManager().getScarResist(player.getUniqueId());
        double cap = cfg("max_resist", level * 0.05);
        double dr = Math.min(resist / 100.0, cap);
        event.setDamage(event.getDamage() * (1.0 - dr));
    }

    @Override public String getDescription() { return "Taking damage builds damage resistance (permanent)."; }
    @Override public String getDescription(int level) {
        return "§7Every 10 dmg taken = +1% DR (cap §a" + (level*5) + "§a%§7)."; }
}
