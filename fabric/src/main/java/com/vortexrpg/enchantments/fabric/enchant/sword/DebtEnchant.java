package com.vortexrpg.enchantments.fabric.enchant.sword;

import com.vortexrpg.enchantments.fabric.core.EnchantRarity;
import com.vortexrpg.enchantments.fabric.core.FabricEnchant;
import com.vortexrpg.enchantments.fabric.core.ItemTarget;
import com.vortexrpg.enchantments.fabric.runtime.Scheduler;
import com.vortexrpg.enchantments.fabric.util.Fx;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;

import java.util.List;

/** Deal 150/175/200% damage; 2s later the wielder takes 30% of the bonus as self-damage. */
public class DebtEnchant extends FabricEnchant {

    private static final double[] MULTIPLIERS = {1.5, 1.75, 2.0};

    public DebtEnchant() {
        super("debt", "Debt", EnchantRarity.EPIC, 3, List.of(ItemTarget.SWORD));
    }

    @Override
    public float onAttack(ServerPlayerEntity attacker, LivingEntity victim, int level, float damage) {
        double selfPct = cfg("self_damage_percent", 0.30);
        long delayTicks = cfgi("delay_ticks", 40);
        double mult = MULTIPLIERS[level - 1];

        double bonus = damage * (mult - 1.0);
        float scaled = (float) (damage * mult);
        float selfDamage = (float) (bonus * selfPct);

        Scheduler.runLater(() -> {
            if (attacker.isAlive() && !attacker.isRemoved()) {
                attacker.damage(serverWorld(attacker), attacker.getDamageSources().generic(), selfDamage);
            }
        }, delayTicks);

        ServerWorld world = serverWorld(victim);
        Fx.particlesAt(world, victim, ParticleTypes.CRIT, 8, 0.3);
        Fx.soundAt(world, victim, SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, 0.8f, 0.8f);
        return scaled;
    }

    @Override
    public String getDescription(int level) {
        int[] pct = {150, 175, 200};
        return "Deal " + pct[level - 1] + "% damage. You take 30% of the bonus as self-damage after 2s.";
    }
}
