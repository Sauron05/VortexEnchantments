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

import java.util.List;
import java.util.Random;

/**
 * Void Touch: 10/15/20% chance on hit to temporarily suppress one of the
 * target's enchantments (removes a random vanilla enchantment for 30 seconds).
 */
public class VoidTouchEnchant extends VortexEnchant {

    private static final Random RANDOM = new Random();

    public VoidTouchEnchant() {
        super("void_touch", "Void Touch", EnchantRarity.MYTHIC, 3, List.of(ItemTarget.SWORD));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        double chance = cfgd("chance", 0.05 + level * 0.05);
        if (RANDOM.nextDouble() > chance) return;

        EntityEquipment eq = victim.getEquipment();
        if (eq == null) return;

        ItemStack held = eq.getItemInMainHand();
        if (held.getType().isAir() || held.getEnchantments().isEmpty()) return;

        var enchants = new java.util.ArrayList<>(held.getEnchantments().entrySet());
        var target = enchants.get(RANDOM.nextInt(enchants.size()));

        int originalLevel = target.getValue();
        held.removeEnchantment(target.getKey());

        ParticleUtil.spawn(victim.getLocation().add(0, 1, 0), Particle.REVERSE_PORTAL, 15, 0.4);
        SoundUtil.play(victim.getLocation(), Sound.ENTITY_ENDERMAN_SCREAM, 0.5f, 2.0f);

        if (victim instanceof Player p) {
            p.sendMessage("§5[Void Touch] §7Your §e" + target.getKey().getKey().getKey() + " §7was suppressed!");
        }
        attacker.sendMessage("§5[Void Touch] §7Suppressed an enchantment from target's weapon!");

        final var enchKey = target.getKey();
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (!victim.isValid() || victim.isDead()) return;
            EntityEquipment eq2 = victim.getEquipment();
            if (eq2 == null) return;
            ItemStack current = eq2.getItemInMainHand();
            if (current.getType().isAir()) return;
            current.addUnsafeEnchantment(enchKey, originalLevel);
            if (victim instanceof Player p) {
                p.sendMessage("§5[Void Touch] §7Your §e" + enchKey.getKey().getKey() + " §7returns.");
            }
        }, cfgi("suppress_ticks", 600));
    }

    @Override
    public String getDescription(int level) {
        int pct = (int) ((0.05 + level * 0.05) * 100);
        return "§7" + pct + "% chance to §5suppress§7 a target's enchantment for 30s.";
    }
}
