package com.vortexrpg.enchantments.enchant.impl.shield;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/** Absorb: Each block while holding shield builds absorption (1% per hit, up to 20/30/40%). */
public class AbsorbEnchant extends VortexEnchant {
    private static final double[] CAP = {0.20, 0.30, 0.40};

    public AbsorbEnchant() { super("absorb", "Absorb", EnchantRarity.RARE, 3, List.of(ItemTarget.SHIELD)); }

    @Override
    public void onDamageTaken(EntityDamageByEntityEvent event, Player player, int level) {
        if (!isEnabled() || !player.isBlocking()) return;
        double stored = plugin.getPlayerDataManager().getDouble(player.getUniqueId(), "absorb_charge");
        double cap = cfg("cap", CAP[level-1]);
        double dr = Math.min(stored, cap);
        event.setDamage(event.getDamage() * (1.0 - dr));
        stored = Math.min(stored + 0.01, cap);
        plugin.getPlayerDataManager().setDouble(player.getUniqueId(), "absorb_charge", stored);
    }

    @Override public String getDescription() { return "Each blocked hit charges a growing DR."; }
    @Override public String getDescription(int level) {
        return "§7Blocking builds §a1%§7 DR per hit (max §a" + (int)(CAP[level-1]*100) + "§a%§7)."; }
}
