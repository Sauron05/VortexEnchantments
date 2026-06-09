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
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

/**
 * Bloodforge: Right-click to sacrifice 3/4/5 hearts of your own HP.
 * Your next 3 attacks deal +50/75/100% bonus damage.
 * Cannot activate below 6 hearts. Charges expire after 10 seconds.
 */
public class BloodforgeEnchant extends VortexEnchant {

    private static final String CHARGES_KEY = "bloodforge_charges";
    private static final String TIMER_KEY = "bloodforge_time";

    public BloodforgeEnchant() {
        super("bloodforge", "Bloodforge", EnchantRarity.MYTHIC, 3, List.of(ItemTarget.AXE));
    }

    @Override
    public void onInteract(PlayerInteractEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!event.getAction().isRightClick()) return;
        if (isOnCooldown(player)) return;

        int existingCharges = plugin.getPlayerDataManager().getInt(player.getUniqueId(), CHARGES_KEY);
        if (existingCharges > 0) return; // already empowered

        double sacrifice = cfgd("sacrifice_hp", 4.0 + level * 2.0);
        double minHp = cfgd("min_hp", 12.0);

        if (player.getHealth() <= minHp) {
            player.sendMessage("§4[Bloodforge] §7Too low HP to sacrifice!");
            return;
        }

        player.setHealth(Math.max(1.0, player.getHealth() - sacrifice));

        int charges = cfgi("charges", 3);
        plugin.getPlayerDataManager().setInt(player.getUniqueId(), CHARGES_KEY, charges);
        plugin.getPlayerDataManager().setInt(player.getUniqueId(), TIMER_KEY,
                (int) ((System.currentTimeMillis() / 100) & 0x7FFFFFFF));

        ParticleUtil.spawn(player.getLocation().add(0, 1, 0), Particle.DAMAGE_INDICATOR, 15, 0.5);
        ParticleUtil.spawn(player.getLocation().add(0, 1, 0), Particle.DUST_PLUME, 10, 0.4);
        SoundUtil.play(player.getLocation(), Sound.ENTITY_WITHER_HURT, 0.5f, 1.5f);
        player.sendMessage("§4[Bloodforge] §7Sacrificed " + (int) sacrifice + " HP! §c" + charges + " empowered hits§7 ready.");

        setCooldownFromConfig(player, "cooldown", 12);
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        int charges = plugin.getPlayerDataManager().getInt(attacker.getUniqueId(), CHARGES_KEY);
        if (charges <= 0) return;

        // Check expiry (10 seconds)
        int storedTime = plugin.getPlayerDataManager().getInt(attacker.getUniqueId(), TIMER_KEY);
        long elapsed = System.currentTimeMillis() - ((long) storedTime * 100);
        if (elapsed > cfgi("expire_ms", 10000)) {
            plugin.getPlayerDataManager().setInt(attacker.getUniqueId(), CHARGES_KEY, 0);
            attacker.sendMessage("§4[Bloodforge] §7Empowerment expired.");
            return;
        }

        double bonus = cfgd("damage_bonus", 0.25 + level * 0.25);
        event.setDamage(event.getDamage() * (1 + bonus));

        charges--;
        plugin.getPlayerDataManager().setInt(attacker.getUniqueId(), CHARGES_KEY, charges);

        ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.DAMAGE_INDICATOR, 10, 0.4);
        SoundUtil.play(attacker.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 0.8f, 0.6f);

        if (charges == 0) {
            attacker.sendMessage("§4[Bloodforge] §7Empowerment consumed!");
        }
    }

    @Override
    public String getDescription(int level) {
        int pct = (int) ((0.25 + level * 0.25) * 100);
        int sacrifice = (int) (4 + level * 2);
        return "§7Sacrifice §c" + sacrifice + " HP §7to empower 3 hits with §c+" + pct + "% §7damage.";
    }
}
