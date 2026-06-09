package com.vortexrpg.enchantments.fabric.enchant.sword;

import com.vortexrpg.enchantments.fabric.core.EnchantRarity;
import com.vortexrpg.enchantments.fabric.core.FabricEnchant;
import com.vortexrpg.enchantments.fabric.core.ItemTarget;
import com.vortexrpg.enchantments.fabric.runtime.PlayerState;
import com.vortexrpg.enchantments.fabric.runtime.Scheduler;
import com.vortexrpg.enchantments.fabric.util.Fx;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

/**
 * Each hit drains saturation from a player target. While they sit at 0 saturation they take
 * +50% damage from this weapon for 6s.
 */
public class DroughtEnchant extends FabricEnchant {

    private static final int[] SATURATION_DRAIN = {3, 4, 5};
    private static final String FLAG = "drought";

    public DroughtEnchant() {
        super("drought", "Drought", EnchantRarity.EPIC, 3, List.of(ItemTarget.SWORD));
    }

    @Override
    public float onAttack(ServerPlayerEntity attacker, LivingEntity victim, int level, float damage) {
        if (!(victim instanceof ServerPlayerEntity target)) return damage;

        int drain = cfgi("saturation_drain", SATURATION_DRAIN[level - 1]);
        double ampPercent = cfg("damage_amp_percent", 50.0);
        long ampTicks = (long) (cfg("amp_duration_seconds", 6.0) * 20);

        float newSat = Math.max(0f, target.getHungerManager().getSaturationLevel() - drain);
        target.getHungerManager().setSaturationLevel(newSat);

        PlayerState targetState = PlayerState.of(target.getUuid());
        if (newSat <= 0f) {
            targetState.setInt(FLAG, 1);
            target.sendMessage(Text.literal("You are parched! Incoming damage increased!")
                    .formatted(Formatting.RED), false);
            Scheduler.runLater(() -> targetState.setInt(FLAG, 0), ampTicks);
        }

        float result = damage;
        if (targetState.getInt(FLAG) == 1) {
            result = (float) (damage * (1.0 + ampPercent / 100.0));
        }

        Fx.particlesAt(serverWorld(victim), victim, ParticleTypes.RAIN, 6, 0.3);
        return result;
    }

    @Override
    public String getDescription(int level) {
        return "Drain " + SATURATION_DRAIN[level - 1] + " saturation per hit. At 0 saturation: +50% dmg for 6s.";
    }
}
