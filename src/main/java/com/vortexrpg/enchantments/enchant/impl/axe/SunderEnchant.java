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
import org.bukkit.inventory.meta.Damageable;

import java.util.List;

/**
 * Sunder: Each hit destroys 10/15/20 durability from ALL armor pieces
 * the target is wearing simultaneously. Destroys defenses fast.
 */
public class SunderEnchant extends VortexEnchant {


    public SunderEnchant() {
        super("sunder", "Sunder", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.AXE));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        int durDmg = cfgi("durability_damage", 5 + level * 5);
        EntityEquipment eq = victim.getEquipment();
        if (eq == null) return;

        int destroyed = 0;
        ItemStack[] armor = {eq.getHelmet(), eq.getChestplate(), eq.getLeggings(), eq.getBoots()};
        for (ItemStack piece : armor) {
            if (piece == null || piece.getType().isAir()) continue;
            if (piece.getItemMeta() instanceof Damageable dmg) {
                dmg.setDamage(dmg.getDamage() + durDmg);
                piece.setItemMeta(dmg);
                destroyed++;
            }
        }

        if (destroyed > 0) {
            ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.SMOKE, 15, 0.4);
            SoundUtil.play(victim.getLocation(), Sound.ENTITY_ITEM_BREAK, 0.7f, 1.2f);
            if (victim instanceof Player p) {
                p.sendMessage("§4[Sunder] §7All armor pieces cracking! §c-" + durDmg + " durability each!");
            }
        }
    }

    @Override
    public String getDescription(int level) {
        int dmg = 5 + level * 5;
        return "§7Hits destroy §c" + dmg + " durability§7 from §eALL§7 armor pieces at once.";
    }
}
