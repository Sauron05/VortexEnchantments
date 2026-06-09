package com.vortexrpg.enchantments.enchant.impl.spear;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.List;

/**
 * Oblivion: On kill, suppress all drops, but grant 3x XP and restore
 * 30/50/70% of the player's max HP. The target is consumed entirely.
 */
public class OblivionEnchant extends VortexEnchant {

    public OblivionEnchant() {
        super("oblivion", "Oblivion", EnchantRarity.MYTHIC, 3, List.of(ItemTarget.SPEAR));
    }

    @Override
    public void onKill(EntityDeathEvent event, Player killer, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        // Suppress drops
        event.getDrops().clear();
        event.setDroppedExp(event.getDroppedExp() * 3);

        // Restore HP
        double healPct = cfgd("heal_percent", 0.10 + level * 0.20);
        double maxHp = killer.getAttribute(Attribute.MAX_HEALTH).getValue();
        double heal = maxHp * healPct;
        killer.setHealth(Math.min(killer.getHealth() + heal, maxHp));

        // Cosmetic: darkness cloud and void particles
        ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.SQUID_INK, 25, 0.6);
        ParticleUtil.drawCircle(victim.getLocation(), 2.0, 16, Particle.PORTAL);
        SoundUtil.play(victim.getLocation(), Sound.ENTITY_WITHER_DEATH, 0.3f, 2.0f);

        killer.sendMessage("§8[Oblivion] §7Target consumed. §a+" + (int) heal + " HP §7restored.");
    }

    @Override
    public String getDescription(int level) {
        int pct = (int) ((0.10 + level * 0.20) * 100);
        return "§7Kill: §8no drops§7, §a3x XP§7, restore §c" + pct + "% §7max HP.";
    }
}
