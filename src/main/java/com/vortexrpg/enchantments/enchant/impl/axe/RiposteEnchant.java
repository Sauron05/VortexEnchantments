package com.vortexrpg.enchantments.enchant.impl.axe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/**
 * Riposte: After being hit, the next attack within 2s deals bonus damage.
 * Bonus: +40/60/80% on the counter-attack.
 */
public class RiposteEnchant extends VortexEnchant {

    private static final String RIPOSTE_KEY = "riposte_ready";

    public RiposteEnchant() {
        super("riposte", "Riposte", EnchantRarity.EPIC, 3, List.of(ItemTarget.AXE));
    }

    @Override
    public void onDamaged(EntityDamageByEntityEvent event, Player victim, Entity attacker, int level) {
        if (!isEnabled()) return;
        long now = System.currentTimeMillis();
        plugin.getPlayerDataManager().setInt(victim.getUniqueId(), RIPOSTE_KEY, (int) ((now / 100) & 0x7FFFFFFF));
        ParticleUtil.spawn(victim.getLocation().add(0, 1.5, 0), Particle.ENCHANTED_HIT, 5, 0.3);
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        int stored = plugin.getPlayerDataManager().getInt(attacker.getUniqueId(), RIPOSTE_KEY);
        if (stored == 0) return;

        long storedTime = (long) stored * 100;
        long now = System.currentTimeMillis();
        long window = cfgi("window_ms", 2000);

        if (now - storedTime > window) {
            plugin.getPlayerDataManager().setInt(attacker.getUniqueId(), RIPOSTE_KEY, 0);
            return;
        }

        double bonus = cfgd("bonus", 0.2 + level * 0.2);
        event.setDamage(event.getDamage() * (1 + bonus));

        ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.ENCHANTED_HIT, 15, 0.5);
        SoundUtil.play(attacker.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 0.8f, 1.3f);
        attacker.sendMessage("§6[Riposte] §7Counter-attack!");

        plugin.getPlayerDataManager().setInt(attacker.getUniqueId(), RIPOSTE_KEY, 0);
    }

    @Override
    public String getDescription(int level) {
        int pct = (int) ((0.2 + level * 0.2) * 100);
        return "§7After being hit, counter-attack within 2s deals §c+" + pct + "% §7damage.";
    }
}
