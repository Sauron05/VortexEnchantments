package com.vortexrpg.enchantments.enchant.impl.spear;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * Echo Strike: Every 3rd consecutive hit on the same target deals
 * 40/60/80% bonus magical damage as an echo of the previous strikes.
 */
public class EchoStrikeEnchant extends VortexEnchant {

    private static final String HIT_KEY = "echostrike_hits_";
    private static final String TARGET_KEY = "echostrike_target";

    public EchoStrikeEnchant() {
        super("echostrike", "Echo Strike", EnchantRarity.RARE, 3, List.of(ItemTarget.SPEAR));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        var pdm = plugin.getPlayerDataManager();
        int targetHash = victim.getUniqueId().hashCode();
        int lastTarget = pdm.getInt(attacker.getUniqueId(), TARGET_KEY);

        int hits;
        if (lastTarget == targetHash) {
            hits = pdm.getInt(attacker.getUniqueId(), HIT_KEY) + 1;
        } else {
            hits = 1;
            pdm.setInt(attacker.getUniqueId(), TARGET_KEY, targetHash);
        }

        if (hits >= 3) {
            double bonus = cfgd("bonus_percent", 0.20 + level * 0.20);
            event.setDamage(event.getDamage() * (1.0 + bonus));
            hits = 0;

            ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.SONIC_BOOM, 1, 0.3);
            SoundUtil.play(victim.getLocation(), Sound.ENTITY_ALLAY_AMBIENT_WITH_ITEM, 0.9f, 0.5f);
        }

        pdm.setInt(attacker.getUniqueId(), HIT_KEY, hits);
    }

    @Override
    public String getDescription(int level) {
        int pct = (int) ((0.20 + level * 0.20) * 100);
        return "§7Every §e3rd hit §7on same target: §d+" + pct + "% §7echo damage.";
    }
}
