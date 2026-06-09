package com.vortexrpg.enchantments.enchant.impl.crossbow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.List;

/**
 * Payload: Bolt carries the offhand item; deploys it on impact.
 */
public class PayloadEnchant extends VortexEnchant {
    public PayloadEnchant() { super("payload", "Payload", EnchantRarity.EPIC, 1, List.of(ItemTarget.CROSSBOW)); }

    @Override
    public void onShoot(EntityShootBowEvent event, Player shooter, int level) {
        if (!isEnabled()) return;
        ItemStack offhand = shooter.getInventory().getItemInOffHand();
        if (offhand == null || offhand.getType() == Material.AIR) return;
        if (event.getProjectile() instanceof AbstractArrow arrow) {
            arrow.setMetadata("payload_item", new FixedMetadataValue(plugin, offhand.clone()));
            shooter.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
        }
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity target, int level) {
        deployPayload(event.getDamager(), target.getLocation());
    }

    private void deployPayload(org.bukkit.entity.Entity projectile, Location loc) {
        if (!(projectile instanceof AbstractArrow arrow)) return;
        if (!arrow.hasMetadata("payload_item")) return;
        ItemStack item = (ItemStack) arrow.getMetadata("payload_item").get(0).value();
        if (item != null && item.getType() != Material.AIR) {
            loc.getWorld().dropItemNaturally(loc, item);
            ParticleUtil.burst(loc, Particle.ITEM, 8, 0.3);
            SoundUtil.play(loc, Sound.ENTITY_ITEM_PICKUP, 1f, 0.8f);
        }
    }

    @Override public String getDescription() { return "Bolt carries your offhand item to the target."; }
    @Override public String getDescription(int level) {
        return "§7Bolt carries your §eoffhand item§7 and drops it at impact."; }
}
