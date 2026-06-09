package com.vortexrpg.enchantments.fabric.combat;

import com.vortexrpg.enchantments.fabric.core.EnchantRegistry;
import com.vortexrpg.enchantments.fabric.core.FabricEnchant;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Map;

/**
 * Entry point used by {@code LivingEntityMixin} to run melee enchant logic and scale damage.
 * Because Fabric has no mutable damage event, each weapon enchant transforms the damage value.
 */
public final class CombatHooks {

    private CombatHooks() {}

    /**
     * Called as a victim is about to take damage. If a player attacked with an enchanted weapon,
     * each weapon enchant's {@link FabricEnchant#onAttack} may scale the value and apply effects.
     */
    public static float modifyIncomingDamage(LivingEntity victim, DamageSource source, float amount) {
        Entity attacker = source.getAttacker();
        if (attacker instanceof ServerPlayerEntity player) {
            ItemStack weapon = player.getMainHandStack();
            Map<FabricEnchant, Integer> enchants = EnchantRegistry.getEnchants(weapon);
            if (!enchants.isEmpty()) {
                for (Map.Entry<FabricEnchant, Integer> e : enchants.entrySet()) {
                    FabricEnchant enchant = e.getKey();
                    if (enchant.isEnabled() && enchant.targetsMatch(weapon)) {
                        amount = enchant.onAttack(player, victim, e.getValue(), amount);
                    }
                }
            }
        }

        // Armour reactions for player victims (no armour enchants in batch 1 yet, hook is live).
        if (victim instanceof ServerPlayerEntity victimPlayer) {
            for (var slot : new net.minecraft.entity.EquipmentSlot[]{
                    net.minecraft.entity.EquipmentSlot.HEAD, net.minecraft.entity.EquipmentSlot.CHEST,
                    net.minecraft.entity.EquipmentSlot.LEGS, net.minecraft.entity.EquipmentSlot.FEET}) {
                ItemStack piece = victimPlayer.getEquippedStack(slot);
                for (Map.Entry<FabricEnchant, Integer> e : EnchantRegistry.getEnchants(piece).entrySet()) {
                    if (e.getKey().isEnabled() && e.getKey().targetsMatch(piece)) {
                        e.getKey().onDamaged(victimPlayer, source, e.getValue());
                    }
                }
            }
        }

        return Math.max(0.0f, amount);
    }
}
