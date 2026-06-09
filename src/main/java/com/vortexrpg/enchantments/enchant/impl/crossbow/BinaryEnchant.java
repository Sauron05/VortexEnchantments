package com.vortexrpg.enchantments.enchant.impl.crossbow;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;

import java.util.List;

/**
 * Binary: Odd shots deal normal physical damage; even shots deal pure magic damage (bypasses armor).
 */
public class BinaryEnchant extends VortexEnchant {
    public BinaryEnchant() { super("binary", "Binary", EnchantRarity.RARE, 1, List.of(ItemTarget.CROSSBOW)); }

    @Override
    public void onShoot(EntityShootBowEvent event, Player shooter, int level) {
        if (!isEnabled()) return;
        int counter = plugin.getPlayerDataManager().getInt(shooter.getUniqueId(), "binary_shot_counter");
        plugin.getPlayerDataManager().setInt(shooter.getUniqueId(), "binary_shot_counter", counter + 1);
        // Store shot parity on arrow as metadata flag
        if (event.getProjectile() instanceof AbstractArrow arrow) {
            arrow.setMetadata("binary_magic", new org.bukkit.metadata.FixedMetadataValue(plugin, (counter + 1) % 2 == 0));
        }
    }

    @Override
    public void onArrowHitEntity(EntityDamageByEntityEvent event, Player shooter, LivingEntity target, int level) {
        if (!isEnabled()) return;
        if (!(event.getDamager() instanceof AbstractArrow arrow)) return;
        if (!arrow.hasMetadata("binary_magic")) return;
        boolean magic = (boolean) arrow.getMetadata("binary_magic").get(0).value();
        if (magic) {
            // Apply as magic damage: cancel event and deal direct health damage
            double dmg = event.getDamage();
            event.setCancelled(true);
            double newHp = Math.max(0, target.getHealth() - dmg);
            target.setHealth(newHp);
        }
    }

    @Override public String getDescription() { return "Alternates physical and magic damage bolts."; }
    @Override public String getDescription(int level) {
        return "§7Odd shots: §fnormal§7. Even shots: §dmagic damage§7 (bypasses armor)."; }
}
