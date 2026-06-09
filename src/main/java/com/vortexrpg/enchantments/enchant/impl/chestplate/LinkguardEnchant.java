package com.vortexrpg.enchantments.enchant.impl.chestplate;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Linkguard: Consecutive hits from the same damage source type reduce damage incrementally.
 */
public class LinkguardEnchant extends VortexEnchant {
    private static final Map<UUID, String> LAST_TYPE = new HashMap<>();
    private static final Map<UUID, Integer> STACKS = new HashMap<>();

    public LinkguardEnchant() { super("linkguard", "Linkguard", EnchantRarity.RARE, 3, List.of(ItemTarget.CHESTPLATE)); }

    @Override
    public void onDamaged(EntityDamageByEntityEvent event, Player victim, Entity attacker, int level) {
        if (!isEnabled()) return;
        String type = attacker.getType().name();
        UUID uid = victim.getUniqueId();
        String lastType = LAST_TYPE.get(uid);
        if (type.equals(lastType)) {
            int stacks = STACKS.getOrDefault(uid, 0) + 1;
            int maxStacks = cfgi("max_stacks", 3 + level);
            stacks = Math.min(stacks, maxStacks);
            STACKS.put(uid, stacks);
            double reductionPer = cfgd("reduction_per_stack", 0.05);
            event.setDamage(event.getDamage() * (1.0 - stacks * reductionPer));
        } else {
            STACKS.put(uid, 0);
        }
        LAST_TYPE.put(uid, type);
    }

    @Override public String getDescription(int level) {
        return "§7Same source type hits deal §a5% §7less each time, up to §a" + (3 + level) + " §7stacks.";
    }
}
