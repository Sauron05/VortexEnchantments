package com.vortexrpg.enchantments.enchant.impl.hammer;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Magnetize: 15/25/35% chance to pull off one random piece of armor from the victim.
 * Dropped at victim's feet.
 */
public class MagnetizeEnchant extends VortexEnchant {

    public MagnetizeEnchant() {
        super("magnetize", "Magnetize", EnchantRarity.RARE, 3, List.of(ItemTarget.HAMMER));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        double chance = cfgd("chance", 0.05 + level * 0.10);
        if (Math.random() > chance) return;

        EntityEquipment equip = victim.getEquipment();
        if (equip == null) return;

        List<Integer> armorSlots = new ArrayList<>();
        ItemStack[] armor = equip.getArmorContents();
        for (int i = 0; i < armor.length; i++) {
            if (armor[i] != null && !armor[i].getType().isAir()) {
                armorSlots.add(i);
            }
        }
        if (armorSlots.isEmpty()) return;

        int slot = armorSlots.get((int) (Math.random() * armorSlots.size()));
        ItemStack stripped = armor[slot].clone();
        armor[slot] = new ItemStack(Material.AIR);
        equip.setArmorContents(armor);

        victim.getWorld().dropItemNaturally(victim.getLocation(), stripped);
        ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.ENCHANTED_HIT, 10, 0.5);
        SoundUtil.play(victim.getLocation(), Sound.BLOCK_CHAIN_BREAK, 0.8f, 1.0f);
    }

    @Override
    public String getDescription(int level) {
        int pct = (int) ((0.05 + level * 0.10) * 100);
        return "§7" + pct + "% chance to §estrip §7a random armor piece on hit.";
    }
}
