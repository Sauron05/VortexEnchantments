package com.vortexrpg.enchantments.fabric.enchant.sword;

import com.vortexrpg.enchantments.fabric.core.EnchantRarity;
import com.vortexrpg.enchantments.fabric.core.FabricEnchant;
import com.vortexrpg.enchantments.fabric.core.ItemTarget;
import com.vortexrpg.enchantments.fabric.runtime.PlayerState;
import com.vortexrpg.enchantments.fabric.util.Fx;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;

import java.util.List;

/** -50% damage normally; after 5s+ idle, the next hit deals 300/350/400% damage. */
public class DormantEnchant extends FabricEnchant {

    private static final double[] BONUS_MULTIPLIERS = {3.0, 3.5, 4.0};

    public DormantEnchant() {
        super("dormant", "Dormant", EnchantRarity.RARE, 3, List.of(ItemTarget.SWORD));
    }

    @Override
    public float onAttack(ServerPlayerEntity attacker, LivingEntity victim, int level, float damage) {
        long idleThresholdMs = (long) (cfg("idle_threshold_seconds", 5.0) * 1000);
        double normalPenalty = cfg("normal_penalty", 0.50);
        double bonusMult = BONUS_MULTIPLIERS[level - 1];

        PlayerState state = PlayerState.of(attacker.getUuid());
        boolean idle = state.isIdle(idleThresholdMs);
        float result;
        if (idle) {
            result = (float) (damage * bonusMult);
            ServerWorld world = serverWorld(attacker);
            Fx.particlesAt(world, attacker, ParticleTypes.SOUL_FIRE_FLAME, 16, 0.4);
            Fx.soundAt(world, attacker, SoundEvents.ENTITY_WARDEN_SONIC_BOOM, 0.6f, 1.5f);
        } else {
            result = (float) (damage * normalPenalty);
        }
        state.recordSwing();
        return result;
    }

    @Override
    public String getDescription(int level) {
        int[] pcts = {300, 350, 400};
        return "-50% damage normally. After 5s idle: " + pcts[level - 1] + "% damage.";
    }
}
