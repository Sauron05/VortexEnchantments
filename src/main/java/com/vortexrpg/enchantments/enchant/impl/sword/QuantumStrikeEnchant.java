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
import java.util.Random;

/**
 * Quantum Strike: Schrödinger's sword. 15/20/25% chance to deal DOUBLE damage,
 * but 10/8/5% chance to deal ZERO damage. Quantum uncertainty.
 */
public class QuantumStrikeEnchant extends VortexEnchant {

    private static final Random RANDOM = new Random();

    public QuantumStrikeEnchant() {
        super("quantum_strike", "Quantum Strike", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.SWORD));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        double doubleChance = cfgd("double_chance", 0.1 + level * 0.05);
        double zeroChance = cfgd("zero_chance", 0.12 - level * 0.02);

        double roll = RANDOM.nextDouble();

        if (roll < zeroChance) {
            event.setDamage(0);
            ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.SMOKE, 10, 0.3);
            SoundUtil.play(attacker.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.5f, 0.5f);
            attacker.sendMessage("§8[Quantum] §7The strike phases through... §c0 damage!");
        } else if (roll < zeroChance + doubleChance) {
            event.setDamage(event.getDamage() * 2.0);
            ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.ENCHANTED_HIT, 20, 0.5);
            SoundUtil.play(attacker.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 2.0f);
            attacker.sendMessage("§b[Quantum] §7Critical quantum collapse! §c2x DAMAGE!");
        }
    }

    @Override
    public String getDescription(int level) {
        int doublePct = (int) ((0.1 + level * 0.05) * 100);
        int zeroPct = (int) ((0.12 - level * 0.02) * 100);
        return "§7" + doublePct + "% chance: §c2x damage§7. " + zeroPct + "% chance: §80 damage§7. Quantum dice.";
    }
}
