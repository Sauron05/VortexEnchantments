package com.vortexrpg.enchantments.enchant.impl.axe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.List;
import java.util.UUID;

/**
 * Weight: Each kill adds +3%/4%/5% damage, -2%/2.5%/3% attack speed per stack (max 8/10/12).
 * Resets on death or 5min idle.
 */
@SuppressWarnings("removal")
public class WeightEnchant extends VortexEnchant {

    private static final double[] DAMAGE_PER_STACK = {0.03, 0.04, 0.05};
    private static final double[] SPEED_PENALTY = {0.02, 0.025, 0.03};
    private static final int[] MAX_STACKS = {8, 10, 12};

    public WeightEnchant() {
        super("weight", "Weight", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.AXE));
    }

    @Override
    public void onKill(EntityDeathEvent event, Player killer, LivingEntity killed, int level) {
        if (!isEnabled()) return;
        var pdm = plugin.getPlayerDataManager();
        int max = cfgi("max_stacks", MAX_STACKS[level - 1]);
        int stacks = pdm.getWeightStacks(killer.getUniqueId());
        if (stacks >= max) return;

        pdm.addWeightStack(killer.getUniqueId());
        int newStacks = pdm.getWeightStacks(killer.getUniqueId());
        applyWeightModifiers(killer, newStacks, level);
        pdm.setLong(killer.getUniqueId(), "weight_last_kill", System.currentTimeMillis());
    }

    @Override
    public void tickPassive(Player player, int level) {
        var pdm = plugin.getPlayerDataManager();
        long lastKill = pdm.getLong(player.getUniqueId(), "weight_last_kill");
        double resetMins = cfg("reset_minutes", 5.0);
        if (System.currentTimeMillis() - lastKill > (long)(resetMins * 60000)) {
            pdm.setWeightStacks(player.getUniqueId(), 0);
            clearWeightModifiers(player);
        }
    }

    private void applyWeightModifiers(Player player, int stacks, int level) {
        clearWeightModifiers(player);
        double damageMod = stacks * cfg("damage_per_stack", DAMAGE_PER_STACK[level - 1]);
        double speedPenalty = stacks * cfg("speed_penalty_per_stack", SPEED_PENALTY[level - 1]);

        applyMod(player, Attribute.ATTACK_DAMAGE, "weight_dmg", damageMod,
            AttributeModifier.Operation.ADD_SCALAR);
        applyMod(player, Attribute.ATTACK_SPEED, "weight_spd", -speedPenalty,
            AttributeModifier.Operation.ADD_SCALAR);
    }

    private void clearWeightModifiers(Player player) {
        removeMod(player, Attribute.ATTACK_DAMAGE, "weight_dmg");
        removeMod(player, Attribute.ATTACK_SPEED, "weight_spd");
    }

    private void applyMod(Player pl, Attribute attr, String key, double val, AttributeModifier.Operation op) {
        AttributeInstance ai = pl.getAttribute(attr);
        if (ai == null) return;
        ai.getModifiers().stream().filter(m -> m.getName().equals(key)).toList().forEach(ai::removeModifier);
        ai.addModifier(new AttributeModifier(UUID.nameUUIDFromBytes(key.getBytes()), key, val, op));
    }

    private void removeMod(Player pl, Attribute attr, String key) {
        AttributeInstance ai = pl.getAttribute(attr);
        if (ai == null) return;
        ai.getModifiers().stream().filter(m -> m.getName().equals(key)).toList().forEach(ai::removeModifier);
    }

    @Override
    public String getDescription() { return "Builds power with kills but slows attack speed over time."; }

    @Override
    public String getDescription(int level) {
        return "§7Per kill: §a+" + (int)(DAMAGE_PER_STACK[level-1]*100) + "%§7 dmg, §c-" + (int)(SPEED_PENALTY[level-1]*100) + "%§7 speed. Max §e" + MAX_STACKS[level-1] + "§7 stacks.";
    }
}
