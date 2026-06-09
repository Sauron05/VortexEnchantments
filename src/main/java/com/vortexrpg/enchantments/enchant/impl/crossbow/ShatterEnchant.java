package com.vortexrpg.enchantments.enchant.impl.crossbow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;

/** Shatter: On hit has 5/8/10% chance to break one random armor piece of target. */
public class ShatterEnchant extends VortexEnchant {
    private static final double[] CHANCE = {5, 8, 10};
    public ShatterEnchant() { super("shatter", "Shatter", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.CROSSBOW)); }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity target, int level) {
        if (!isEnabled()) return;
        if (!MathUtil.chance(CHANCE[level-1])) return;
        EntityEquipment equip = target.getEquipment();
        if (equip == null) return;
        EquipmentSlot[] armorSlots = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
        List<EquipmentSlot> worn = new java.util.ArrayList<>();
        for (EquipmentSlot slot : armorSlots) {
            ItemStack item = equip.getItem(slot);
            if (item != null && item.getType().getMaxDurability() > 0) worn.add(slot);
        }
        if (worn.isEmpty()) return;
        EquipmentSlot slot = worn.get(new Random().nextInt(worn.size()));
        equip.setItem(slot, null);
        SoundUtil.play(target.getLocation(), Sound.ENTITY_ITEM_BREAK, 1f, 1f);
    }

    @Override public String getDescription() { return "Bolts may shatter target's armor."; }
    @Override public String getDescription(int level) {
        return "§7Bolts have §c" + (int)CHANCE[level-1] + "%§7 chance to destroy a random armor piece on target."; }
}
