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
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Karmic Debt: Stores 20/30/40% of damage YOU take. Your next attack
 * releases all stored damage as bonus. Resets after 10 seconds.
 */
public class KarmicDebtEnchant extends VortexEnchant {

    private final ConcurrentHashMap<UUID, double[]> karmicStore = new ConcurrentHashMap<>();

    public KarmicDebtEnchant() {
        super("karmic_debt", "Karmic Debt", EnchantRarity.RARE, 3, List.of(ItemTarget.SWORD));
    }

    @Override
    public void onDamaged(EntityDamageByEntityEvent event, Player victim, org.bukkit.entity.Entity attacker, int level) {
        if (!isEnabled()) return;

        double storeRatio = cfgd("store_ratio", 0.1 + level * 0.1);
        double maxStored = cfgd("max_stored", 20.0);

        UUID uuid = victim.getUniqueId();
        double stored = 0;
        double[] data = karmicStore.get(uuid);
        if (data != null) stored = data[0];

        stored = Math.min(stored + event.getDamage() * storeRatio, maxStored);
        karmicStore.put(uuid, new double[]{stored, System.currentTimeMillis()});

        ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.SOUL, 3, 0.3);
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        UUID uuid = attacker.getUniqueId();
        double[] data = karmicStore.remove(uuid);
        if (data == null) return;

        long elapsed = System.currentTimeMillis() - (long) data[1];
        if (elapsed > cfgd("timeout_ms", 10000)) return;

        double bonus = data[0];
        if (bonus < 1.0) return;

        event.setDamage(event.getDamage() + bonus);
        ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.SOUL_FIRE_FLAME, 15, 0.5);
        SoundUtil.play(attacker.getLocation(), Sound.ENTITY_WARDEN_SONIC_CHARGE, 0.7f, 1.5f);
        attacker.sendMessage("§c[Karma] §7Released §c" + String.format("%.1f", bonus) + " §7stored damage!");
    }

    @Override
    public String getDescription(int level) {
        int pct = (int) ((0.1 + level * 0.1) * 100);
        return "§7Store §c" + pct + "%§7 of damage taken. Next attack releases it as §4bonus damage§7.";
    }
}
