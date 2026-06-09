package com.vortexrpg.enchantments.fabric.event;

import com.vortexrpg.enchantments.fabric.core.EnchantRegistry;
import com.vortexrpg.enchantments.fabric.core.FabricEnchant;
import com.vortexrpg.enchantments.fabric.runtime.Cooldowns;
import com.vortexrpg.enchantments.fabric.runtime.PlayerState;
import com.vortexrpg.enchantments.fabric.runtime.Scheduler;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Registers all Fabric API event handlers and routes them to the enchant hooks. */
public final class EventDispatch {

    private static int tickCounter = 0;

    private EventDispatch() {}

    public static void register() {
        // Kills
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
            Entity attacker = source.getAttacker();
            if (attacker instanceof ServerPlayerEntity killer) {
                ItemStack weapon = killer.getMainHandStack();
                for (Map.Entry<FabricEnchant, Integer> e : EnchantRegistry.getEnchants(weapon).entrySet()) {
                    if (e.getKey().isEnabled() && e.getKey().targetsMatch(weapon)) {
                        e.getKey().onKill(killer, entity, e.getValue());
                    }
                }
            }
        });

        // Block breaking
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            if (player instanceof ServerPlayerEntity sp && world instanceof ServerWorld sw) {
                ItemStack tool = sp.getMainHandStack();
                for (Map.Entry<FabricEnchant, Integer> e : EnchantRegistry.getEnchants(tool).entrySet()) {
                    if (e.getKey().isEnabled() && e.getKey().targetsMatch(tool)) {
                        e.getKey().onBlockBreak(sp, sw, pos, e.getValue());
                    }
                }
            }
        });

        // Right-click use
        UseItemCallback.EVENT.register((player, world, hand) -> {
            if (player instanceof ServerPlayerEntity sp) {
                ItemStack stack = sp.getStackInHand(hand);
                for (Map.Entry<FabricEnchant, Integer> e : EnchantRegistry.getEnchants(stack).entrySet()) {
                    if (e.getKey().isEnabled() && e.getKey().targetsMatch(stack)) {
                        e.getKey().onInteract(sp, stack, e.getValue());
                    }
                }
            }
            return ActionResult.PASS;
        });

        // Tick loop: scheduler + once-per-second passive tick
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            Scheduler.tick(server);
            tickCounter++;
            if (tickCounter % 20 == 0) {
                for (ServerPlayerEntity sp : server.getPlayerManager().getPlayerList()) {
                    passiveTick(sp);
                }
            }
        });

        // Cleanup transient state on disconnect
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            PlayerState.clear(handler.player.getUuid());
            Cooldowns.clear(handler.player.getUuid());
        });
    }

    private static void passiveTick(ServerPlayerEntity sp) {
        List<ItemStack> items = new ArrayList<>();
        items.add(sp.getMainHandStack());
        items.add(sp.getStackInHand(Hand.OFF_HAND));
        for (EquipmentSlot slot : new EquipmentSlot[]{
                EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
            items.add(sp.getEquippedStack(slot));
        }
        for (ItemStack item : items) {
            for (Map.Entry<FabricEnchant, Integer> e : EnchantRegistry.getEnchants(item).entrySet()) {
                if (e.getKey().isEnabled()) {
                    e.getKey().tickPassive(sp, e.getValue());
                }
            }
        }
    }
}
