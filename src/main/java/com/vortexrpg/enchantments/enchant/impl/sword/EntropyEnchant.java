package com.vortexrpg.enchantments.enchant.impl.sword;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

/**
 * Entropy: Each swing increases attack speed +5% but decreases damage by 3%/2.5%/2%.
 * Stacks reset after 4s of no swinging.
 */
@SuppressWarnings("removal")
public class EntropyEnchant extends VortexEnchant {

    private static final double[] DAMAGE_PENALTY = {0.03, 0.025, 0.02};
    private static final String SPEED_KEY = "entropy_speed";
    @SuppressWarnings("unused")
    private static final String DMGMOD_KEY = "entropy_dmg";

    public EntropyEnchant() {
        super("entropy", "Entropy", EnchantRarity.RARE, 3, List.of(ItemTarget.SWORD));
    }

    @Override
    public void onInteract(PlayerInteractEvent event, Player player, ItemStack item, int level) {
        // Track swing via left-click air / block (animation event would be ideal but this is a fallback)
        applyEntropySwing(player, level);
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        applyEntropySwing(attacker, level);

        int stacks = plugin.getPlayerDataManager().getEntropyStacks(attacker.getUniqueId());
        double penalty = DAMAGE_PENALTY[level - 1] * stacks;
        if (penalty > 0 && event.getDamage() > 0) {
            event.setDamage(Math.max(0.1, event.getDamage() * (1.0 - penalty)));
        }
    }

    private void applyEntropySwing(Player player, int level) {
        var pdm = plugin.getPlayerDataManager();
        double resetSecs = cfg("reset_seconds", 4.0);
        int maxStacks = cfgi("max_stacks", 20);

        long lastSwing = pdm.getLastSwingEntropyTime(player.getUniqueId());
        long now = System.currentTimeMillis();

        if (now - lastSwing > (long)(resetSecs * 1000)) {
            // Reset stacks & modifiers
            pdm.setEntropyStacks(player.getUniqueId(), 0);
            removeAttributeMod(player, Attribute.ATTACK_SPEED, SPEED_KEY);
        }

        int stacks = Math.min(pdm.getEntropyStacks(player.getUniqueId()) + 1, maxStacks);
        pdm.setEntropyStacks(player.getUniqueId(), stacks);
        pdm.setLastSwingEntropyTime(player.getUniqueId(), now);

        double speedBonus = cfg("speed_bonus_per_swing", 0.05) * stacks;
        applyAttributeMod(player, Attribute.ATTACK_SPEED, SPEED_KEY, speedBonus,
            AttributeModifier.Operation.ADD_SCALAR);
    }

    private void applyAttributeMod(Player player, Attribute attr, String key, double amount,
                                   AttributeModifier.Operation op) {
        AttributeInstance ai = player.getAttribute(attr);
        if (ai == null) return;
        ai.getModifiers().stream()
            .filter(m -> m.getName().equals(key))
            .forEach(ai::removeModifier);
        ai.addModifier(new AttributeModifier(UUID.nameUUIDFromBytes(key.getBytes()), key, amount, op));
    }

    private void removeAttributeMod(Player player, Attribute attr, String key) {
        AttributeInstance ai = player.getAttribute(attr);
        if (ai == null) return;
        ai.getModifiers().stream()
            .filter(m -> m.getName().equals(key))
            .toList()
            .forEach(ai::removeModifier);
    }

    @Override
    public String getDescription() { return "Each swing increases speed but reduces damage. Resets after idling."; }

    @Override
    public String getDescription(int level) {
        double pen = DAMAGE_PENALTY[level - 1] * 100;
        return "§7Per swing: §a+5%§7 attack speed, §c-" + pen + "%§7 damage. Reset after §e4s§7 idle.";
    }
}
