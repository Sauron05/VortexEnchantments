package com.vortexrpg.enchantments.fabric.core;

import com.vortexrpg.enchantments.fabric.runtime.Cooldowns;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Base class for every native-Fabric enchantment. Subclasses override only the hooks they use.
 *
 * <p>This mirrors the Paper {@code VortexEnchant} model, but the hooks take Fabric/Yarn types and
 * {@link #onAttack} returns the (possibly scaled) outgoing damage because Fabric has no mutable
 * damage event — the combat mixin feeds the value through each enchant.
 */
public abstract class FabricEnchant {

    protected final String id;
    protected final String displayName;
    protected final EnchantRarity rarity;
    protected final int maxLevel;
    protected final List<ItemTarget> targets;

    private boolean enabled = true;
    protected final Map<String, Object> config = new HashMap<>();

    protected FabricEnchant(String id, String displayName, EnchantRarity rarity, int maxLevel, List<ItemTarget> targets) {
        this.id = id;
        this.displayName = displayName;
        this.rarity = rarity;
        this.maxLevel = maxLevel;
        this.targets = List.copyOf(targets);
    }

    // ── Hooks (override as needed) ────────────────────────────────────────

    /** Melee hit with the enchanted weapon. Return the (possibly scaled) damage. */
    public float onAttack(ServerPlayerEntity attacker, LivingEntity victim, int level, float damage) {
        return damage;
    }

    /** The holder killed a living entity with the enchanted weapon. */
    public void onKill(ServerPlayerEntity killer, LivingEntity victim, int level) {}

    /** The holder broke a block with the enchanted tool. */
    public void onBlockBreak(ServerPlayerEntity player, ServerWorld world, BlockPos pos, int level) {}

    /** Fired once per second for each equipped enchanted item. */
    public void tickPassive(ServerPlayerEntity player, int level) {}

    /** Right-click use with the enchanted item in hand. */
    public void onInteract(ServerPlayerEntity player, ItemStack stack, int level) {}

    /** The holder (wearing the enchanted armour) took damage. */
    public void onDamaged(ServerPlayerEntity victim, DamageSource source, int level) {}

    // ── Description ───────────────────────────────────────────────────────
    public abstract String getDescription(int level);
    public String getDescription() { return getDescription(1); }

    // ── Config helpers (defaults until per-enchant config is loaded) ───────
    protected double cfg(String key, double def) {
        Object v = config.get(key);
        return v instanceof Number n ? n.doubleValue() : def;
    }
    protected int cfgi(String key, int def) {
        Object v = config.get(key);
        return v instanceof Number n ? n.intValue() : def;
    }
    protected boolean cfgb(String key, boolean def) {
        Object v = config.get(key);
        return v instanceof Boolean b ? b : def;
    }

    // ── Cooldown helpers ──────────────────────────────────────────────────
    protected boolean isOnCooldown(PlayerEntity player) {
        return Cooldowns.isOnCooldown(player.getUuid(), id);
    }
    protected long remainingCooldown(PlayerEntity player) {
        return Cooldowns.remainingMillis(player.getUuid(), id);
    }
    protected void setCooldownSeconds(PlayerEntity player, double seconds) {
        Cooldowns.set(player.getUuid(), id, (long) (seconds * 1000));
    }

    // ── Helpers ───────────────────────────────────────────────────────────
    protected static ServerWorld serverWorld(LivingEntity entity) {
        return (ServerWorld) entity.getEntityWorld();
    }

    public boolean targetsMatch(ItemStack stack) {
        for (ItemTarget t : targets) {
            if (t.matches(stack)) return true;
        }
        return false;
    }

    public Text loreLine(int level) {
        String roman = maxLevel == 1 ? "" : " " + toRoman(level);
        return Text.literal(displayName + roman).formatted(rarity.getColor());
    }

    private static String toRoman(int n) {
        return switch (n) {
            case 1 -> "I"; case 2 -> "II"; case 3 -> "III"; case 4 -> "IV"; case 5 -> "V";
            case 6 -> "VI"; case 7 -> "VII"; case 8 -> "VIII"; case 9 -> "IX"; case 10 -> "X";
            default -> String.valueOf(n);
        };
    }

    // ── Getters ───────────────────────────────────────────────────────────
    public String getId() { return id; }
    public String getDisplayName() { return displayName; }
    public EnchantRarity getRarity() { return rarity; }
    public int getMaxLevel() { return maxLevel; }
    public List<ItemTarget> getTargets() { return targets; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}
