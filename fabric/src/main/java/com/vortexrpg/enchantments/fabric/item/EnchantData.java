package com.vortexrpg.enchantments.fabric.item;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Reads/writes VortexEnchantments enchant data on an {@link ItemStack}, stored under the
 * {@code minecraft:custom_data} component in a {@code VortexEnchantments} sub-compound
 * ({@code id -> level}). This persists with the item and survives drops/relogs.
 */
public final class EnchantData {

    public static final String ROOT = "VortexEnchantments";

    private EnchantData() {}

    /** @return ordered map of {@code enchantId -> level} present on the stack. */
    public static Map<String, Integer> read(ItemStack stack) {
        Map<String, Integer> result = new LinkedHashMap<>();
        if (stack == null || stack.isEmpty()) return result;
        NbtComponent comp = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (comp == null) return result;
        NbtCompound sub = comp.copyNbt().getCompoundOrEmpty(ROOT);
        for (String key : sub.getKeys()) {
            int level = sub.getInt(key, 0);
            if (level > 0) result.put(key, level);
        }
        return result;
    }

    public static int level(ItemStack stack, String id) {
        return read(stack).getOrDefault(id, 0);
    }

    public static boolean has(ItemStack stack, String id) {
        return level(stack, id) > 0;
    }

    public static void write(ItemStack stack, String id, int level) {
        if (stack == null || stack.isEmpty()) return;
        NbtComponent.set(DataComponentTypes.CUSTOM_DATA, stack, nbt -> {
            NbtCompound sub = nbt.getCompoundOrEmpty(ROOT);
            sub.putInt(id, level);
            nbt.put(ROOT, sub);
        });
    }

    public static void remove(ItemStack stack, String id) {
        if (stack == null || stack.isEmpty()) return;
        NbtComponent comp = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (comp == null) return;
        NbtComponent.set(DataComponentTypes.CUSTOM_DATA, stack, nbt -> {
            NbtCompound sub = nbt.getCompoundOrEmpty(ROOT);
            sub.remove(id);
            nbt.put(ROOT, sub);
        });
    }
}
