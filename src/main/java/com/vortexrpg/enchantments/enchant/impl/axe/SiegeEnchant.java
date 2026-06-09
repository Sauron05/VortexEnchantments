package com.vortexrpg.enchantments.enchant.impl.axe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Siege: Deals bonus damage to armored targets.
 * More armor pieces = more bonus. +8/12/16% per armor piece worn.
 */
public class SiegeEnchant extends VortexEnchant {

    public SiegeEnchant() {
        super("siege", "Siege", EnchantRarity.RARE, 3, List.of(ItemTarget.AXE));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        EntityEquipment equip = victim.getEquipment();
        if (equip == null) return;

        int armorPieces = 0;
        if (isArmor(equip.getHelmet())) armorPieces++;
        if (isArmor(equip.getChestplate())) armorPieces++;
        if (isArmor(equip.getLeggings())) armorPieces++;
        if (isArmor(equip.getBoots())) armorPieces++;

        if (armorPieces == 0) return;

        double bonusPerPiece = cfgd("bonus_per_piece", 0.04 + level * 0.04);
        double multiplier = 1 + armorPieces * bonusPerPiece;
        event.setDamage(event.getDamage() * multiplier);

        if (armorPieces >= 3) {
            ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.ENCHANTED_HIT, 10, 0.4);
            SoundUtil.play(victim.getLocation(), Sound.ITEM_ARMOR_EQUIP_CHAIN, 0.6f, 0.5f);
        }
    }

    private boolean isArmor(ItemStack item) {
        return item != null && !item.getType().isAir();
    }

    @Override
    public String getDescription(int level) {
        int pct = (int) ((0.04 + level * 0.04) * 100);
        return "§7+" + pct + "% damage per armor piece target wears (max §c" + (pct * 4) + "%§7).";
    }
}
