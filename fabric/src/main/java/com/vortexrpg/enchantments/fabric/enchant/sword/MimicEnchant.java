package com.vortexrpg.enchantments.fabric.enchant.sword;

import com.vortexrpg.enchantments.fabric.core.EnchantRarity;
import com.vortexrpg.enchantments.fabric.core.FabricEnchant;
import com.vortexrpg.enchantments.fabric.core.ItemTarget;
import com.vortexrpg.enchantments.fabric.util.Fx;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;

import java.util.List;

/** On kill, gain a 5s buff matching the slain mob type. */
public class MimicEnchant extends FabricEnchant {

    public MimicEnchant() {
        super("mimic", "Mimic", EnchantRarity.LEGENDARY, 1, List.of(ItemTarget.SWORD));
    }

    @Override
    public void onKill(ServerPlayerEntity killer, LivingEntity victim, int level) {
        int ticks = cfgi("buff_duration_ticks", 100);
        EntityType<?> type = victim.getType();

        if (type == EntityType.ZOMBIE) {
            apply(killer, StatusEffects.STRENGTH, ticks, 0);
        } else if (type == EntityType.SKELETON) {
            apply(killer, StatusEffects.SPEED, ticks, 1);
        } else if (type == EntityType.SPIDER || type == EntityType.CAVE_SPIDER) {
            apply(killer, StatusEffects.JUMP_BOOST, ticks, 1);
        } else if (type == EntityType.BLAZE) {
            apply(killer, StatusEffects.FIRE_RESISTANCE, ticks, 0);
        } else if (type == EntityType.ENDERMAN) {
            apply(killer, StatusEffects.SPEED, ticks, 1);
        } else if (type == EntityType.CREEPER) {
            apply(killer, StatusEffects.RESISTANCE, ticks, 0);
        } else if (type == EntityType.WITHER_SKELETON) {
            apply(killer, StatusEffects.STRENGTH, ticks, 0);
        } else if (type == EntityType.GUARDIAN || type == EntityType.ELDER_GUARDIAN) {
            apply(killer, StatusEffects.WATER_BREATHING, ticks, 0);
            apply(killer, StatusEffects.DOLPHINS_GRACE, ticks, 0);
        } else if (type == EntityType.PHANTOM) {
            apply(killer, StatusEffects.SLOW_FALLING, ticks, 0);
            apply(killer, StatusEffects.SPEED, ticks, 0);
        } else {
            apply(killer, StatusEffects.REGENERATION, ticks, 0);
        }

        Fx.particlesAt(serverWorld(killer), killer, ParticleTypes.ENCHANT, 20, 0.4);
        Fx.soundAt(serverWorld(killer), killer, SoundEvents.ENTITY_ILLUSIONER_CAST_SPELL, 0.8f, 1.2f);
    }

    private void apply(ServerPlayerEntity player, RegistryEntry<StatusEffect> effect, int ticks, int amplifier) {
        player.addStatusEffect(new StatusEffectInstance(effect, ticks, amplifier, false, true));
    }

    @Override
    public String getDescription(int level) {
        return "Gain a 5s buff mimicking your kill (Zombie=Strength, Skeleton=Speed, ...).";
    }
}
