package com.vortexrpg.enchantments.enchant.impl.spear;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Vaulting: Right-click to pole-vault forward 4/6/8 blocks in your
 * look direction, launching into the air as if using the spear as a pole.
 */
public class VaultingEnchant extends VortexEnchant {

    public VaultingEnchant() {
        super("vaulting", "Vaulting", EnchantRarity.UNCOMMON, 3, List.of(ItemTarget.SPEAR));
    }

    @Override
    public void onInteract(PlayerInteractEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!event.getAction().isRightClick()) return;
        if (isOnCooldown(player)) return;

        double distance = cfgd("distance", 2.0 + level * 2.0);
        double lift = cfgd("lift", 0.6 + level * 0.2);

        Vector dir = player.getLocation().getDirection().normalize();
        Vector velocity = dir.multiply(distance * 0.25).setY(lift);
        player.setVelocity(velocity);

        ParticleUtil.spawn(player.getLocation(), Particle.CLOUD, 15, 0.4);
        SoundUtil.play(player.getLocation(), Sound.ENTITY_HORSE_JUMP, 0.8f, 1.2f);

        setCooldownFromConfig(player, "cooldown", 5);
    }

    @Override
    public String getDescription(int level) {
        int dist = 2 + level * 2;
        return "§7Right-click to §bpole-vault §7forward §e" + dist + " blocks§7.";
    }
}
