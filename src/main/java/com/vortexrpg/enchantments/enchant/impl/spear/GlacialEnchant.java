package com.vortexrpg.enchantments.enchant.impl.spear;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

/**
 * Glacial: Hits apply Slowness II and freeze ice at the target's feet.
 * Ice melts after 3 seconds.
 */
public class GlacialEnchant extends VortexEnchant {

    public GlacialEnchant() {
        super("glacial", "Glacial", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.SPEAR));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        int slowTicks = cfgi("slow_ticks", 40 + level * 20);
        victim.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, slowTicks, 1, false, true));

        Location feet = victim.getLocation();
        Block block = feet.getBlock();
        if (block.getType() == Material.AIR) {
            block.setType(Material.ICE);
            SoundUtil.play(feet, Sound.BLOCK_GLASS_PLACE, 0.8f, 1.5f);

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (block.getType() == Material.ICE) {
                        block.setType(Material.AIR);
                    }
                }
            }.runTaskLater(JavaPlugin.getProvidingPlugin(getClass()), 60);
        }

        ParticleUtil.spawn(feet.add(0, 0.5, 0), Particle.SNOWFLAKE, 12, 0.5);
    }

    @Override
    public String getDescription(int level) {
        int secs = (40 + level * 20) / 20;
        return "§7Hits apply §bSlowness II §7(" + secs + "s) + §fice §7at target's feet.";
    }
}
