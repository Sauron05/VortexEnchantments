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
 * Impaler: Stacking pierce marks on the target. At 3 marks the target
 * takes a burst of bonus damage (30/50/70% of the triggering hit).
 */
public class ImpalerEnchant extends VortexEnchant {

    private static final String MARK_KEY = "impaler_marks_";

    public ImpalerEnchant() {
        super("impaler", "Impaler", EnchantRarity.COMMON, 3, List.of(ItemTarget.SPEAR));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        String key = MARK_KEY + victim.getUniqueId();
        int marks = plugin.getPlayerDataManager().getInt(attacker.getUniqueId(), key) + 1;

        if (marks >= 3) {
            double bonus = cfgd("burst_percent", 0.20 + level * 0.10);
            event.setDamage(event.getDamage() * (1.0 + bonus));
            plugin.getPlayerDataManager().setInt(attacker.getUniqueId(), key, 0);

            ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.CRIT, 20, 0.5);
            SoundUtil.play(victim.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 0.9f, 1.4f);
        } else {
            plugin.getPlayerDataManager().setInt(attacker.getUniqueId(), key, marks);
            ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.ENCHANTED_HIT, 5, 0.3);
        }
    }

    @Override
    public String getDescription(int level) {
        int pct = (int) ((0.20 + level * 0.10) * 100);
        return "§7Every §e3rd hit §7deals §c+" + pct + "% §7burst damage.";
    }
}
