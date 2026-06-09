package com.vortexrpg.enchantments.enchant.impl.sword;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Antimatter: On hit, destroy 5/8/12 durability from a random armor piece
 * the target is wearing. Eats through equipment.
 */
public class AntimatterEnchant extends VortexEnchant {

    private static final Random RANDOM = new Random();

    public AntimatterEnchant() {
        super("antimatter", "Antimatter", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.SWORD));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        int durabilityDamage = cfgi("durability_damage", 3 + level * 3);
        EntityEquipment eq = victim.getEquipment();
        if (eq == null) return;

        List<ItemStack> armor = new ArrayList<>();
        if (eq.getHelmet() != null && !eq.getHelmet().getType().isAir()) armor.add(eq.getHelmet());
        if (eq.getChestplate() != null && !eq.getChestplate().getType().isAir()) armor.add(eq.getChestplate());
        if (eq.getLeggings() != null && !eq.getLeggings().getType().isAir()) armor.add(eq.getLeggings());
        if (eq.getBoots() != null && !eq.getBoots().getType().isAir()) armor.add(eq.getBoots());

        if (armor.isEmpty()) return;

        ItemStack target = armor.get(RANDOM.nextInt(armor.size()));
        if (target.getItemMeta() instanceof Damageable dmg) {
            dmg.setDamage(dmg.getDamage() + durabilityDamage);
            target.setItemMeta(dmg);

            ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.SMOKE, 10, 0.3);
            SoundUtil.play(victim.getLocation(), Sound.ENTITY_ITEM_BREAK, 0.5f, 1.5f);

            if (victim instanceof Player p) {
                p.sendMessage("§5[Antimatter] §7Your armor is disintegrating! §c-" + durabilityDamage + " durability");
            }
        }
    }

    @Override
    public String getDescription(int level) {
        int dmg = 3 + level * 3;
        return "§7Hits destroy §c" + dmg + " durability§7 from a random armor piece.";
    }
}
