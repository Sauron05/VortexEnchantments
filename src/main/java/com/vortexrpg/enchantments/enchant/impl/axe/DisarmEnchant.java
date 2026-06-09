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
 * Disarm: Chance to force the target to drop their held item.
 * Chance: 8/12/16%.
 */
public class DisarmEnchant extends VortexEnchant {

    public DisarmEnchant() {
        super("disarm", "Disarm", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.AXE));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        double chance = cfgd("chance", 0.04 + level * 0.04);
        if (Math.random() > chance) return;

        EntityEquipment equip = victim.getEquipment();
        if (equip == null) return;

        ItemStack held = equip.getItemInMainHand();
        if (held.getType().isAir()) return;

        victim.getWorld().dropItemNaturally(victim.getLocation(), held.clone());
        equip.setItemInMainHand(null);

        ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.ENCHANTED_HIT, 12, 0.5);
        SoundUtil.play(victim.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 0.8f);

        if (victim instanceof Player p) {
            p.sendMessage("§c[Disarm] §7Your weapon was knocked from your hands!");
        }
        attacker.sendMessage("§c[Disarm] §7Target disarmed!");
    }

    @Override
    public String getDescription(int level) {
        int pct = (int) ((0.04 + level * 0.04) * 100);
        return "§7" + pct + "% chance to force target to §cdrop their weapon§7.";
    }
}
