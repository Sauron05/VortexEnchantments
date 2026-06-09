package com.vortexrpg.enchantments.fabric.core;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.HashSet;
import java.util.Set;

/** Maps logical enchant targets to the concrete {@link Item}s that can carry them. */
public enum ItemTarget {

    SWORD(Set.of(Items.WOODEN_SWORD, Items.STONE_SWORD, Items.IRON_SWORD,
            Items.GOLDEN_SWORD, Items.DIAMOND_SWORD, Items.NETHERITE_SWORD)),
    AXE(Set.of(Items.WOODEN_AXE, Items.STONE_AXE, Items.IRON_AXE,
            Items.GOLDEN_AXE, Items.DIAMOND_AXE, Items.NETHERITE_AXE)),
    BOW(Set.of(Items.BOW)),
    CROSSBOW(Set.of(Items.CROSSBOW)),
    TRIDENT(Set.of(Items.TRIDENT)),
    MACE(Set.of(Items.MACE)),
    PICKAXE(Set.of(Items.WOODEN_PICKAXE, Items.STONE_PICKAXE, Items.IRON_PICKAXE,
            Items.GOLDEN_PICKAXE, Items.DIAMOND_PICKAXE, Items.NETHERITE_PICKAXE)),
    SHOVEL(Set.of(Items.WOODEN_SHOVEL, Items.STONE_SHOVEL, Items.IRON_SHOVEL,
            Items.GOLDEN_SHOVEL, Items.DIAMOND_SHOVEL, Items.NETHERITE_SHOVEL)),
    HOE(Set.of(Items.WOODEN_HOE, Items.STONE_HOE, Items.IRON_HOE,
            Items.GOLDEN_HOE, Items.DIAMOND_HOE, Items.NETHERITE_HOE)),
    HELMET(Set.of(Items.LEATHER_HELMET, Items.CHAINMAIL_HELMET, Items.IRON_HELMET,
            Items.GOLDEN_HELMET, Items.DIAMOND_HELMET, Items.NETHERITE_HELMET, Items.TURTLE_HELMET)),
    CHESTPLATE(Set.of(Items.LEATHER_CHESTPLATE, Items.CHAINMAIL_CHESTPLATE, Items.IRON_CHESTPLATE,
            Items.GOLDEN_CHESTPLATE, Items.DIAMOND_CHESTPLATE, Items.NETHERITE_CHESTPLATE, Items.ELYTRA)),
    LEGGINGS(Set.of(Items.LEATHER_LEGGINGS, Items.CHAINMAIL_LEGGINGS, Items.IRON_LEGGINGS,
            Items.GOLDEN_LEGGINGS, Items.DIAMOND_LEGGINGS, Items.NETHERITE_LEGGINGS)),
    BOOTS(Set.of(Items.LEATHER_BOOTS, Items.CHAINMAIL_BOOTS, Items.IRON_BOOTS,
            Items.GOLDEN_BOOTS, Items.DIAMOND_BOOTS, Items.NETHERITE_BOOTS)),
    SHIELD(Set.of(Items.SHIELD)),
    ELYTRA(Set.of(Items.ELYTRA)),
    FISHING_ROD(Set.of(Items.FISHING_ROD)),
    MELEE(null),
    TOOL(null),
    ARMOR(null);

    private final Set<Item> items;

    ItemTarget(Set<Item> items) {
        this.items = items;
    }

    public Set<Item> getItems() {
        return switch (this) {
            case MELEE -> union(SWORD, AXE);
            case TOOL -> union(PICKAXE, SHOVEL, HOE);
            case ARMOR -> union(HELMET, CHESTPLATE, LEGGINGS, BOOTS);
            default -> items;
        };
    }

    public boolean matches(Item item) {
        Set<Item> set = getItems();
        return set != null && set.contains(item);
    }

    public boolean matches(ItemStack stack) {
        return stack != null && !stack.isEmpty() && matches(stack.getItem());
    }

    private static Set<Item> union(ItemTarget... targets) {
        Set<Item> set = new HashSet<>();
        for (ItemTarget t : targets) set.addAll(t.getItems());
        return set;
    }
}
