package com.vortexrpg.enchantments.enchant.impl.sword;

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

/** Deal 150/175/200% damage; 2s later wielder takes 30% of bonus damage as self-damage. */
public class DebtEnchant extends VortexEnchant {

    private static final double[] MULTIPLIERS = {1.5, 1.75, 2.0};

    public DebtEnchant() {
        super("debt", "Debt", EnchantRarity.EPIC, 3, List.of(ItemTarget.SWORD));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        double selfPct = cfg("self_damage_percent", 0.30);
        long delayTicks = cfgi("delay_ticks", 40);
        double mult = MULTIPLIERS[level - 1];

        double baseDamage = event.getDamage();
        double bonus = baseDamage * (mult - 1.0);
        event.setDamage(baseDamage * mult);

        double selfDamage = bonus * selfPct;
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (attacker.isOnline() && attacker.isValid() && !attacker.isDead()) {
                attacker.damage(selfDamage);
            }
        }, delayTicks);

        ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.CRIT, 8, 0.3);
        SoundUtil.play(victim.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 0.8f, 0.8f);
    }

    @Override
    public String getDescription() { return "Deals bonus damage but reflects 30% of the bonus back to you 2s later."; }

    @Override
    public String getDescription(int level) {
        String[] d = {
            "Deal §c150% §7damage. You receive §c30% §7of bonus as self-damage after 2s.",
            "Deal §c175% §7damage. You receive §c30% §7of bonus as self-damage after 2s.",
            "Deal §c200% §7damage. You receive §c30% §7of bonus as self-damage after 2s."
        };
        return d[level - 1];
    }
}
