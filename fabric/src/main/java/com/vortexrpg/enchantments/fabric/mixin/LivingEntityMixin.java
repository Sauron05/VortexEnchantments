package com.vortexrpg.enchantments.fabric.mixin;

import com.vortexrpg.enchantments.fabric.combat.CombatHooks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Hooks the server damage pipeline so weapon enchants can scale outgoing melee damage and
 * trigger on-hit effects. {@code argsOnly} targets the {@code float amount} parameter.
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @ModifyVariable(
            method = "damage(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/damage/DamageSource;F)Z",
            at = @At("HEAD"),
            argsOnly = true
    )
    private float vortexenchantments$scaleDamage(float amount, ServerWorld world, DamageSource source) {
        return CombatHooks.modifyIncomingDamage((LivingEntity) (Object) this, source, amount);
    }
}
