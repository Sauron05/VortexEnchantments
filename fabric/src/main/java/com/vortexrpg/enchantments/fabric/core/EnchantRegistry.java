package com.vortexrpg.enchantments.fabric.core;

import com.vortexrpg.enchantments.fabric.item.EnchantData;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Central registry of all native-Fabric enchantments and item read/apply helpers. */
public final class EnchantRegistry {

    private static final Map<String, FabricEnchant> BY_ID = new LinkedHashMap<>();

    private EnchantRegistry() {}

    public static void register(FabricEnchant enchant) {
        BY_ID.put(enchant.getId(), enchant);
    }

    public static FabricEnchant byId(String id) {
        return BY_ID.get(id);
    }

    public static List<FabricEnchant> all() {
        return new ArrayList<>(BY_ID.values());
    }

    public static int count() {
        return BY_ID.size();
    }

    /** All registered Vortex enchants present on the given stack ({@code enchant -> level}). */
    public static Map<FabricEnchant, Integer> getEnchants(ItemStack stack) {
        Map<FabricEnchant, Integer> result = new LinkedHashMap<>();
        if (stack == null || stack.isEmpty()) return result;
        for (Map.Entry<String, Integer> e : EnchantData.read(stack).entrySet()) {
            FabricEnchant enchant = BY_ID.get(e.getKey());
            if (enchant != null) result.put(enchant, e.getValue());
        }
        return result;
    }

    /** Apply (or update) an enchant on a stack, refresh the glint and the lore block. */
    public static void apply(ItemStack stack, FabricEnchant enchant, int level) {
        if (stack == null || stack.isEmpty()) return;
        int clamped = Math.max(1, Math.min(level, enchant.getMaxLevel()));
        EnchantData.write(stack, enchant.getId(), clamped);
        stack.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
        rebuildLore(stack);
    }

    /** Rebuild the lore lines for every Vortex enchant on the stack. */
    public static void rebuildLore(ItemStack stack) {
        Map<FabricEnchant, Integer> enchants = getEnchants(stack);
        List<Text> lore = new ArrayList<>();
        for (Map.Entry<FabricEnchant, Integer> e : enchants.entrySet()) {
            lore.add(e.getKey().loreLine(e.getValue()));
        }
        stack.set(DataComponentTypes.LORE, new LoreComponent(lore));
    }
}
