package com.vortexrpg.enchantments.fabric.enchant.sword;

import com.vortexrpg.enchantments.fabric.core.EnchantRarity;
import com.vortexrpg.enchantments.fabric.core.FabricEnchant;
import com.vortexrpg.enchantments.fabric.core.ItemTarget;
import com.vortexrpg.enchantments.fabric.runtime.PlayerState;
import com.vortexrpg.enchantments.fabric.util.Fx;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;

/** Each kill within the window adds a stacking damage bonus; resets after 10s without a kill. */
public class ThirstEnchant extends FabricEnchant {

    private static final double[] BONUS_PER_STACK = {0.10, 0.12, 0.15};

    public ThirstEnchant() {
        super("thirst", "Thirst", EnchantRarity.EPIC, 3, List.of(ItemTarget.SWORD));
    }

    @Override
    public void onKill(ServerPlayerEntity killer, LivingEntity victim, int level) {
        PlayerState.of(killer.getUuid()).recordKill();
        Fx.particlesAt(serverWorld(killer), killer, ParticleTypes.ENCHANTED_HIT, 6, 0.3);
    }

    @Override
    public float onAttack(ServerPlayerEntity attacker, LivingEntity victim, int level, float damage) {
        long windowMs = (long) (cfg("kill_window_seconds", 10.0) * 1000);
        int maxStacks = cfgi("max_stacks", 10);
        int stacks = Math.min(PlayerState.of(attacker.getUuid()).recentKillCount(windowMs), maxStacks);
        if (stacks <= 0) return damage;
        double bonusPct = BONUS_PER_STACK[level - 1] * stacks;
        return (float) (damage * (1.0 + bonusPct));
    }

    @Override
    public String getDescription(int level) {
        int pct = (int) (BONUS_PER_STACK[level - 1] * 100);
        return "+" + pct + "% damage per kill within 10s. Max 10 stacks.";
    }
}
