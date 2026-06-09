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
 * Cascade Realm: Each consecutive hit within 3 seconds increases damage by 10/15/20%.
 * Stacks up to 5x. Resets if you don't hit within the window.
 */
public class CascadeRealmEnchant extends VortexEnchant {

    private final ConcurrentHashMap<UUID, long[]> cascadeData = new ConcurrentHashMap<>();

    public CascadeRealmEnchant() {
        super("cascade_realm", "Cascade Realm", EnchantRarity.MYTHIC, 3, List.of(ItemTarget.SWORD));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        double bonusPerStack = cfgd("bonus_per_stack", 0.05 + level * 0.05);
        int maxStacks = cfgi("max_stacks", 5);
        long windowMillis = (long) (cfgd("window_seconds", 3.0) * 1000);

        UUID uuid = attacker.getUniqueId();
        long now = System.currentTimeMillis();

        long[] data = cascadeData.get(uuid);
        int stacks;

        if (data != null && (now - data[0]) < windowMillis) {
            stacks = Math.min((int) data[1] + 1, maxStacks);
        } else {
            stacks = 1;
        }

        cascadeData.put(uuid, new long[]{now, stacks});

        if (stacks > 1) {
            double multiplier = 1.0 + (stacks * bonusPerStack);
            event.setDamage(event.getDamage() * multiplier);

            int pct = (int) (stacks * bonusPerStack * 100);
            ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.ENCHANTED_HIT, stacks * 3, 0.4);

            if (stacks >= maxStacks) {
                SoundUtil.play(attacker.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 2.0f);
                attacker.sendMessage("§c[Cascade] §7MAX STACK! §c+" + pct + "% damage!");
            }
        }
    }

    @Override
    public String getDescription(int level) {
        int pct = (int) ((0.05 + level * 0.05) * 100);
        return "§7Consecutive hits: §c+" + pct + "%§7 damage per stack. Max §e5x§7. Resets after §e3s§7.";
    }
}
