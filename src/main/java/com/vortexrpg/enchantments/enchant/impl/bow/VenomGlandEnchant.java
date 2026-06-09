package com.vortexrpg.enchantments.enchant.impl.bow;

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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.List;

/**
 * VenomGland: Each arrow hit on the same target stacks venom — cumulative
 * Poison effect that grows stronger with repeated hits (up to 3/4/5 stacks).
 */
public class VenomGlandEnchant extends VortexEnchant {

    private static final Map<UUID, Integer> VENOM_STACKS = new HashMap<>();
    private static final Map<UUID, Long> LAST_HIT = new HashMap<>();

    public VenomGlandEnchant() {
        super("venomgland", "Venom Gland", EnchantRarity.RARE, 3, List.of(ItemTarget.BOW));
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        UUID id = victim.getUniqueId();
        long now = System.currentTimeMillis();
        long window = cfgi("window_ms", 8000);
        int maxStacks = cfgi("max_stacks", 2 + level);

        Long last = LAST_HIT.get(id);
        if (last == null || now - last > window) {
            VENOM_STACKS.put(id, 1);
        } else {
            VENOM_STACKS.merge(id, 1, (a, b) -> a + b);
        }
        LAST_HIT.put(id, now);

        int stacks = Math.min(VENOM_STACKS.getOrDefault(id, 1), maxStacks);
        int duration = cfgi("poison_ticks", 60) + stacks * 20;
        int amp = Math.min(stacks - 1, 2);

        victim.addPotionEffect(new PotionEffect(PotionEffectType.POISON, duration, amp, false, true));
        ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.ITEM_SLIME, stacks * 4, 0.3);
    }

    @Override
    public String getDescription(int level) {
        int max = 2 + level;
        return "§7Arrows stack §2venom §7on target (max §e" + max + "§7 stacks).";
    }
}
