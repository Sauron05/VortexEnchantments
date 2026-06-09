package com.vortexrpg.enchantments.enchant.impl.shield;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

import java.util.List;

/** Echo Block: Perfect block creates sonic pulse pushing nearby mobs. */
public class EchoBlockEnchant extends VortexEnchant {

    public EchoBlockEnchant() { super("echo_block", "Echo Block", EnchantRarity.RARE, 3, List.of(ItemTarget.SHIELD)); }

    @Override
    public void onDamageTaken(EntityDamageByEntityEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!player.isBlocking()) return;
        if (isOnCooldown(player)) return;
        double radius = cfg("radius", 3.0 + level);
        double force = cfg("force", 0.4 + level * 0.2);
        for (LivingEntity e : MathUtil.getNearbyLiving(player.getLocation(), radius)) {
            if (e.equals(player)) continue;
            Vector push = e.getLocation().toVector().subtract(player.getLocation().toVector()).normalize().multiply(force).setY(0.3);
            e.setVelocity(push);
        }
        SoundUtil.play(player.getLocation(), Sound.ENTITY_WARDEN_SONIC_BOOM, 0.5f, 1.5f);
        ParticleUtil.drawCircle(player.getLocation().add(0, 1, 0), radius, 30, Particle.SONIC_BOOM);
        setCooldownFromConfig(player, "cooldown", 8);
    }

    @Override public String getDescription() { return "Perfect block creates sonic pulse."; }
    @Override public String getDescription(int level) {
        return "§7Block: §dsonic pulse§7 pushes mobs in §e" + (int)(3 + level) + "b§7."; }
}
