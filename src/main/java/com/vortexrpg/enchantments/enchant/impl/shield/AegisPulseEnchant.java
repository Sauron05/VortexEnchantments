package com.vortexrpg.enchantments.enchant.impl.shield;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/** AegisPulse: Blocking 3/2/1 consecutive hits triggers a Resistance II pulse for 5s. */
public class AegisPulseEnchant extends VortexEnchant {
    private static final int[] HITS = {3, 2, 1};

    public AegisPulseEnchant() { super("aegis_pulse", "Aegis Pulse", EnchantRarity.EPIC, 3, List.of(ItemTarget.SHIELD)); }

    @Override
    public void onDamageTaken(EntityDamageByEntityEvent event, Player player, int level) {
        if (!isEnabled() || !player.isBlocking()) return;
        int hits = plugin.getPlayerDataManager().getInt(player.getUniqueId(), "aegis_pulse_hits") + 1;
        int required = cfgi("required_hits", HITS[level-1]);
        if (hits >= required) {
            hits = 0;
            player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 100, 1));
            com.vortexrpg.enchantments.util.ParticleUtil.ring(player.getLocation(), org.bukkit.Particle.CLOUD, 16, 1.5f);
        }
        plugin.getPlayerDataManager().setInt(player.getUniqueId(), "aegis_pulse_hits", hits);
    }

    @Override public String getDescription() { return "Blocking hits build resistance burst."; }
    @Override public String getDescription(int level) {
        return "§7Block §a" + HITS[level-1] + "§7 hits: §aResistance II§7 for §a5s§7."; }
}
