package com.vortexrpg.enchantments.enchant.impl.axe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;
import java.util.UUID;

/**
 * Overload: 200%/225%/250% damage but axe enters a 3/2.5/2s cooldown.
 */
@SuppressWarnings("removal")
public class OverloadEnchant extends VortexEnchant {

    private static final double[] MULTIPLIER = {2.0, 2.25, 2.5};
    private static final double[] COOLDOWN_SECS = {3.0, 2.5, 2.0};

    public OverloadEnchant() {
        super("overload", "Overload", EnchantRarity.MYTHIC, 3, List.of(ItemTarget.AXE));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        if (isOnCooldown(attacker)) return;

        double mult = cfg("damage_multiplier", MULTIPLIER[level - 1]);
        event.setDamage(event.getDamage() * mult);

        double cdSecs = cfg("cooldown_seconds", COOLDOWN_SECS[level - 1]);
        setCooldownSeconds(attacker, cdSecs);

        // Visual: slow down attack speed
        applyAttackSpeedPenalty(attacker, cdSecs);
    }

    private void applyAttackSpeedPenalty(Player player, double durationSecs) {
        AttributeInstance ai = player.getAttribute(Attribute.ATTACK_SPEED);
        if (ai == null) return;
        String key = "overload_penalty";
        ai.getModifiers().stream().filter(m -> m.getName().equals(key)).toList().forEach(ai::removeModifier);
        ai.addModifier(new AttributeModifier(UUID.nameUUIDFromBytes(key.getBytes()), key, -100.0, AttributeModifier.Operation.ADD_SCALAR));
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            ai.getModifiers().stream().filter(m -> m.getName().equals(key)).toList().forEach(ai::removeModifier);
        }, (long)(durationSecs * 20));
    }

    @Override
    public String getDescription() { return "Massive damage burst followed by a cooldown."; }

    @Override
    public String getDescription(int level) {
        return "§7Deal §e" + (int)(MULTIPLIER[level-1]*100) + "%§7 damage, then §c" + COOLDOWN_SECS[level-1] + "s§7 attack cooldown.";
    }
}
