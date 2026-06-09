package com.vortexrpg.enchantments.fabric.runtime;

import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.List;

/**
 * Minimal server-thread scheduler (Fabric has no Bukkit-style scheduler).
 * Tasks are advanced once per server tick from {@code ServerTickEvents.END_SERVER_TICK}.
 */
public final class Scheduler {

    private record Task(Runnable action, long runAtTick, long periodTicks) {}

    private static final List<Task> TASKS = new ArrayList<>();
    private static long currentTick = 0;

    private Scheduler() {}

    /** Run once after {@code delayTicks} server ticks. */
    public static synchronized void runLater(Runnable action, long delayTicks) {
        TASKS.add(new Task(action, currentTick + Math.max(0, delayTicks), 0));
    }

    /** Run repeatedly every {@code periodTicks}, starting after {@code delayTicks}. */
    public static synchronized void runTimer(Runnable action, long delayTicks, long periodTicks) {
        TASKS.add(new Task(action, currentTick + Math.max(0, delayTicks), Math.max(1, periodTicks)));
    }

    /** Advance the scheduler by one tick and fire due tasks. Call once per server tick. */
    public static synchronized void tick(MinecraftServer server) {
        currentTick++;
        if (TASKS.isEmpty()) return;
        List<Task> due = new ArrayList<>();
        for (Task t : TASKS) {
            if (t.runAtTick() <= currentTick) due.add(t);
        }
        if (due.isEmpty()) return;
        TASKS.removeAll(due);
        for (Task t : due) {
            try {
                t.action().run();
            } catch (Throwable ignored) {
                // a misbehaving enchant must never crash the tick loop
            }
            if (t.periodTicks() > 0) {
                TASKS.add(new Task(t.action(), currentTick + t.periodTicks(), t.periodTicks()));
            }
        }
    }

    public static synchronized void reset() {
        TASKS.clear();
        currentTick = 0;
    }
}
