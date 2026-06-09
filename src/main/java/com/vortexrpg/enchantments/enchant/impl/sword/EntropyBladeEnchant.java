package com.vortexrpg.enchantments.enchant.impl.sword;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Entropy Blade: Each hit weakens target's natural regeneration by 20/30/40%
 * for 5 seconds. Stacks up to 100%, completely stopping natural regen.
 */
public class EntropyBladeEnchant extends VortexEnchant {

    private final ConcurrentHashMap<UUID, Integer> regenStacks = new ConcurrentHashMap<>();

    public EntropyBladeEnchant() {
        super("entropy_blade", "Entropy Blade", EnchantRarity.EPIC, 3, List.of(ItemTarget.SWORD));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        int stackPct = cfgi("stack_percent", 10 + level * 10);
        int maxPct = 100;
        int durationTicks = cfgi("duration_ticks", 100);

        UUID victimId = victim.getUniqueId();
        int currentPct = regenStacks.getOrDefault(victimId, 0);
        int newPct = Math.min(currentPct + stackPct, maxPct);
        regenStacks.put(victimId, newPct);

        if (newPct >= 60) {
            victim.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, durationTicks, newPct / 25, false, false));
        }

        ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.SMOKE, 5 + newPct / 10, 0.3);

        if (victim instanceof Player p) {
            p.sendMessage("§4[Entropy] §7Regeneration weakened by §c" + newPct + "%§7!");
        }

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            Integer current = regenStacks.get(victimId);
            if (current != null) {
                int reduced = current - stackPct;
                if (reduced <= 0) {
                    regenStacks.remove(victimId);
                } else {
                    regenStacks.put(victimId, reduced);
                }
            }
        }, durationTicks);
    }

    @Override
    public String getDescription(int level) {
        int pct = 10 + level * 10;
        return "§7Each hit reduces target regen by §c" + pct + "%§7. Stacks to §c100%§7 (no regen).";
    }
}
