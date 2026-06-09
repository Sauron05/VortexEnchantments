package com.vortexrpg.enchantments.enchant.impl.axe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Warcry: Right-click to unleash a battle cry. All enemies within 8/10/12 blocks
 * are pushed back and get Weakness for 3 seconds. Allies get Strength.
 */
public class WarcryEnchant extends VortexEnchant {

    public WarcryEnchant() {
        super("warcry", "Warcry", EnchantRarity.EPIC, 3, List.of(ItemTarget.AXE));
    }

    @Override
    public void onInteract(PlayerInteractEvent event, Player player, ItemStack item, int level) {
        if (!isEnabled()) return;
        if (!event.getAction().isRightClick()) return;
        if (isOnCooldown(player)) return;

        double cooldown = cfgd("cooldown_seconds", 20.0);
        double radius = cfgd("radius", 6.0 + level * 2.0);
        int effectTicks = cfgi("effect_ticks", 60);
        double pushStrength = cfgd("push_strength", 1.0);

        setCooldownSeconds(player, cooldown);

        ParticleUtil.drawCircle(player.getLocation(), radius, 30, Particle.SONIC_BOOM);
        SoundUtil.play(player.getLocation(), Sound.ENTITY_RAVAGER_ROAR, 1.0f, 1.2f);

        for (Entity e : player.getNearbyEntities(radius, radius, radius)) {
            if (!(e instanceof LivingEntity le)) continue;

            Vector push = e.getLocation().toVector().subtract(player.getLocation().toVector()).normalize().multiply(pushStrength);
            push.setY(0.3);
            e.setVelocity(push);

            le.addPotionEffect(new org.bukkit.potion.PotionEffect(
                org.bukkit.potion.PotionEffectType.WEAKNESS, effectTicks, 0, false, false));
        }

        player.sendMessage("§6[Warcry] §7Your battle cry echoes! Enemies weakened!");
    }

    @Override
    public String getDescription(int level) {
        int rad = 6 + level * 2;
        return "§7Right-click: §6battle cry§7 pushes enemies + §7Weakness in §e" + rad + " blocks§7.";
    }
}
