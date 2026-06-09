package com.vortexrpg.enchantments.enchant.impl.axe;

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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * Concussion: Heavy axe blows have 30/40/50% chance to stun the target,
 * applying Slowness IV + Mining Fatigue III + Blindness for 1.5s.
 */
public class ConcussionEnchant extends VortexEnchant {

    public ConcussionEnchant() {
        super("concussion", "Concussion", EnchantRarity.RARE, 3, List.of(ItemTarget.AXE));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        double chance = cfgd("chance", 0.2 + level * 0.1);
        if (Math.random() > chance) return;

        int stunTicks = cfgi("stun_ticks", 30);

        victim.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, stunTicks, 3, false, false));
        victim.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, stunTicks, 2, false, false));
        victim.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, stunTicks, 0, false, false));

        ParticleUtil.spawn(victim.getLocation().add(0, 2, 0), Particle.CRIT, 10, 0.3);
        SoundUtil.play(victim.getLocation(), Sound.ENTITY_PLAYER_ATTACK_KNOCKBACK, 0.8f, 0.5f);

        if (victim instanceof Player p) {
            p.sendMessage("§6[Concussion] §7You're stunned!");
        }
    }

    @Override
    public String getDescription(int level) {
        int pct = (int) ((0.2 + level * 0.1) * 100);
        return "§7" + pct + "% chance to §6stun§7 target for §e1.5s§7 (Slow IV + Blind + Fatigue).";
    }
}
