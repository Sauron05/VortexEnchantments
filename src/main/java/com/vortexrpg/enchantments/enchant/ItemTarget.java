package com.vortexrpg.enchantments.enchant;

import org.bukkit.Material;
import java.util.Set;

public enum ItemTarget {

    SWORD(Set.of(
        Material.WOODEN_SWORD, Material.STONE_SWORD, Material.IRON_SWORD,
        Material.GOLDEN_SWORD, Material.DIAMOND_SWORD, Material.NETHERITE_SWORD
    )),
    AXE(Set.of(
        Material.WOODEN_AXE, Material.STONE_AXE, Material.IRON_AXE,
        Material.GOLDEN_AXE, Material.DIAMOND_AXE, Material.NETHERITE_AXE
    )),
    BOW(Set.of(Material.BOW)),
    CROSSBOW(Set.of(Material.CROSSBOW)),
    TRIDENT(Set.of(Material.TRIDENT)),
    SPEAR(Set.of(Material.TRIDENT)),
    HAMMER(Set.of(Material.MACE)),
    PICKAXE(Set.of(
        Material.WOODEN_PICKAXE, Material.STONE_PICKAXE, Material.IRON_PICKAXE,
        Material.GOLDEN_PICKAXE, Material.DIAMOND_PICKAXE, Material.NETHERITE_PICKAXE
    )),
    SHOVEL(Set.of(
        Material.WOODEN_SHOVEL, Material.STONE_SHOVEL, Material.IRON_SHOVEL,
        Material.GOLDEN_SHOVEL, Material.DIAMOND_SHOVEL, Material.NETHERITE_SHOVEL
    )),
    HOE(Set.of(
        Material.WOODEN_HOE, Material.STONE_HOE, Material.IRON_HOE,
        Material.GOLDEN_HOE, Material.DIAMOND_HOE, Material.NETHERITE_HOE
    )),
    HELMET(Set.of(
        Material.LEATHER_HELMET, Material.CHAINMAIL_HELMET, Material.IRON_HELMET,
        Material.GOLDEN_HELMET, Material.DIAMOND_HELMET, Material.NETHERITE_HELMET,
        Material.TURTLE_HELMET
    )),
    CHESTPLATE(Set.of(
        Material.LEATHER_CHESTPLATE, Material.CHAINMAIL_CHESTPLATE, Material.IRON_CHESTPLATE,
        Material.GOLDEN_CHESTPLATE, Material.DIAMOND_CHESTPLATE, Material.NETHERITE_CHESTPLATE,
        Material.ELYTRA
    )),
    LEGGINGS(Set.of(
        Material.LEATHER_LEGGINGS, Material.CHAINMAIL_LEGGINGS, Material.IRON_LEGGINGS,
        Material.GOLDEN_LEGGINGS, Material.DIAMOND_LEGGINGS, Material.NETHERITE_LEGGINGS
    )),
    BOOTS(Set.of(
        Material.LEATHER_BOOTS, Material.CHAINMAIL_BOOTS, Material.IRON_BOOTS,
        Material.GOLDEN_BOOTS, Material.DIAMOND_BOOTS, Material.NETHERITE_BOOTS
    )),
    SHIELD(Set.of(Material.SHIELD)),
    ELYTRA(Set.of(Material.ELYTRA)),
    FISHING_ROD(Set.of(Material.FISHING_ROD)),
    MELEE(null), // SWORD + AXE
    TOOL(null),  // PICKAXE + SHOVEL + HOE
    ARMOR(null); // HELMET + CHESTPLATE + LEGGINGS + BOOTS

    private final Set<Material> materials;

    ItemTarget(Set<Material> materials) {
        this.materials = materials;
    }

    public Set<Material> getMaterials() {
        if (this == MELEE) {
            var s = new java.util.HashSet<>(SWORD.getMaterials());
            s.addAll(AXE.getMaterials());
            return s;
        }
        if (this == TOOL) {
            var s = new java.util.HashSet<>(PICKAXE.getMaterials());
            s.addAll(SHOVEL.getMaterials());
            s.addAll(HOE.getMaterials());
            return s;
        }
        if (this == ARMOR) {
            var s = new java.util.HashSet<>(HELMET.getMaterials());
            s.addAll(CHESTPLATE.getMaterials());
            s.addAll(LEGGINGS.getMaterials());
            s.addAll(BOOTS.getMaterials());
            return s;
        }
        return materials;
    }

    public boolean matches(Material material) {
        return getMaterials() != null && getMaterials().contains(material);
    }

    public static ItemTarget fromMaterial(Material material) {
        for (ItemTarget target : values()) {
            if (target.matches(material)) return target;
        }
        return null;
    }
}
