package com.vortexrpg.enchantments.enchant.impl.helmet;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.util.List;

/** Halo: Players within X blocks receive a small passive heal every second. Aura enchantment. */
public class HaloEnchant extends VortexEnchant {
    public HaloEnchant() { super("halo", "Halo", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.HELMET)); }

    @Override
    public void tickPassive(Player player, int level) {
        if (!isEnabled()) return;
        double radius = cfgd("radius", 4.0 + level * 2.0);
        double heal = cfgd("heal", 0.5);
        for (org.bukkit.entity.LivingEntity e : player.getWorld().getNearbyLivingEntities(player.getLocation(), radius)) {
            if (!(e instanceof Player p)) continue;
            if (p.equals(player)) continue;
            double maxHp = p.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue();
            if (p.getHealth() < maxHp) {
                p.setHealth(Math.min(maxHp, p.getHealth() + heal));
            }
        }
        ParticleUtil.spawn(player.getLocation().add(0, 2.3, 0), Particle.END_ROD, 3, 0.3);
    }

    @Override public String getDescription(int level) {
        return "§7Nearby allies within §a" + (int)(4 + level * 2) + " §7blocks are slowly healed.";
    }
}
