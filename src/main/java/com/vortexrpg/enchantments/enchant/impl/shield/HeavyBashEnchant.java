package com.vortexrpg.enchantments.enchant.impl.shield;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.List;

/** Heavy Bash: Shield bash knocks enemy airborne. */
public class HeavyBashEnchant extends VortexEnchant {

    public HeavyBashEnchant() { super("heavy_bash", "Heavy Bash", EnchantRarity.RARE, 3, List.of(ItemTarget.SHIELD)); }

    @Override
    public void onInteract(PlayerInteractEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!event.getAction().isRightClick()) return;
        if (isOnCooldown(player)) return;
        double damage = cfg("damage", 2.0 + level);
        double range = cfg("range", 2.5);
        double launch = cfg("launch", 0.3 + level * 0.15);
        for (LivingEntity e : MathUtil.getNearbyLiving(player.getLocation(), range)) {
            if (e.equals(player)) continue;
            var dir = e.getLocation().toVector().subtract(player.getLocation().toVector());
            if (player.getLocation().getDirection().angle(dir) < Math.PI / 3) {
                e.damage(damage, player);
                e.setVelocity(new Vector(0, launch, 0).add(dir.normalize().multiply(0.3)));
                setCooldownFromConfig(player, "cooldown", 5);
                return;
            }
        }
    }

    @Override public String getDescription() { return "Shield bash launches enemies airborne."; }
    @Override public String getDescription(int level) {
        return "§7Bash: §c" + (int)(2 + level) + "♥§7 + launch enemy airborne."; }
}
