package com.vortexrpg.enchantments.enchant.impl.trident;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Harpoon: Thrown trident lodges in target. Right-click pull: if target is smaller → pull them to you, else fly to them.
 */
public class HarpoonEnchant extends VortexEnchant {
    public HarpoonEnchant() { super("harpoon", "Harpoon", EnchantRarity.RARE, 3, List.of(ItemTarget.TRIDENT)); }

    @Override
    public void onTridentHit(EntityDamageByEntityEvent event, Player thrower, LivingEntity target, int level) {
        if (!isEnabled()) return;
        target.setMetadata("harpoon_source", new FixedMetadataValue(plugin, thrower.getUniqueId().toString()));
        thrower.setMetadata("harpoon_target", new FixedMetadataValue(plugin, target.getUniqueId().toString()));
    }

    @Override
    public void onInteract(PlayerInteractEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!player.hasMetadata("harpoon_target")) return;
        String targetId = (String) player.getMetadata("harpoon_target").get(0).value();
        Entity target = null;
        for (Entity e : player.getWorld().getEntities()) {
            if (e.getUniqueId().toString().equals(targetId)) { target = e; break; }
        }
        if (target == null) { player.removeMetadata("harpoon_target", plugin); return; }
        double sizeThreshold = cfg("size_threshold", 1.5);
        double yankForce = cfg("yank_force", 2.5);
        if (target instanceof LivingEntity le && le.getHeight() < sizeThreshold) {
            // Pull target toward player
            Vector pull = player.getLocation().toVector().subtract(le.getLocation().toVector()).normalize().multiply(yankForce);
            le.setVelocity(pull);
        } else {
            // Fly player toward target
            Vector fly = target.getLocation().toVector().subtract(player.getLocation().toVector()).normalize().multiply(yankForce);
            player.setVelocity(fly);
        }
        player.removeMetadata("harpoon_target", plugin);
        target.removeMetadata("harpoon_source", plugin);
        SoundUtil.play(player.getLocation(), Sound.ENTITY_FISHING_BOBBER_RETRIEVE, 1f, 0.8f);
    }

    @Override public String getDescription() { return "Thrown trident harpooons target; right-click to yank."; }
    @Override public String getDescription(int level) {
        return "§7Throw lodges in target. §eRight-click§7: pull them to you (if small) or fly to them."; }
}
