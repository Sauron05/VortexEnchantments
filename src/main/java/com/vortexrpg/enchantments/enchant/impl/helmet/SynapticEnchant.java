package com.vortexrpg.enchantments.enchant.impl.helmet;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.List;

/**
 * Synaptic: Each hit taken increases your attack speed for 5 seconds (stacks).
 */
public class SynapticEnchant extends VortexEnchant {
    private static final Map<UUID, Integer> STACKS = new HashMap<>();
    private static final Map<UUID, Long> LAST_HIT = new HashMap<>();

    public SynapticEnchant() { super("synaptic", "Synaptic", EnchantRarity.EPIC, 3, List.of(ItemTarget.HELMET)); }

    @Override
    public void onDamaged(EntityDamageByEntityEvent event, Player victim, Entity attacker, int level) {
        if (!isEnabled()) return;
        UUID id = victim.getUniqueId();
        long now = System.currentTimeMillis();
        Long last = LAST_HIT.get(id);
        if (last != null && now - last > 5000) STACKS.remove(id);

        int stacks = Math.min(STACKS.merge(id, 1, (a, b) -> a + b), cfgi("max_stacks", 5));
        LAST_HIT.put(id, now);

        double bonus = cfgd("speed_per_stack", 0.04 * level) * stacks;
        var attr = victim.getAttribute(org.bukkit.attribute.Attribute.ATTACK_SPEED);
        if (attr == null) return;

        var existing = attr.getModifiers().stream()
                .filter(m -> m.getKey().toString().equals("vortex:synaptic"))
                .findFirst().orElse(null);
        if (existing != null) attr.removeModifier(existing);
        attr.addModifier(new org.bukkit.attribute.AttributeModifier(
                org.bukkit.NamespacedKey.fromString("vortex:synaptic"),
                bonus,
                org.bukkit.attribute.AttributeModifier.Operation.ADD_NUMBER));
    }

    @Override public String getDescription(int level) {
        return "§7Each hit taken increases attack speed. Max 5 stacks.";
    }
}
