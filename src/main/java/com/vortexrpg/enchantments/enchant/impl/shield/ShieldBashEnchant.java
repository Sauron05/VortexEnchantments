package com.vortexrpg.enchantments.enchant.impl.shield;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.MathUtil;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

/** Shield Bash: Right-click does small melee damage to nearest mob. */
public class ShieldBashEnchant extends VortexEnchant {

    public ShieldBashEnchant() { super("shield_bash", "Shield Bash", EnchantRarity.COMMON, 3, List.of(ItemTarget.SHIELD)); }

    @Override
    public void onInteract(PlayerInteractEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (!event.getAction().isRightClick()) return;
        if (isOnCooldown(player)) return;
        double damage = cfg("damage", 1.0 + level);
        double range = cfg("range", 2.0);
        for (LivingEntity e : MathUtil.getNearbyLiving(player.getLocation(), range)) {
            if (e.equals(player)) continue;
            var dir = e.getLocation().toVector().subtract(player.getLocation().toVector());
            if (player.getLocation().getDirection().angle(dir) < Math.PI / 3) {
                e.damage(damage, player);
                setCooldownFromConfig(player, "cooldown", 3);
                return;
            }
        }
    }

    @Override public String getDescription() { return "Right-click to bash nearby enemies."; }
    @Override public String getDescription(int level) {
        return "§7Bash: §c" + (int)(1 + level) + "♥§7 to nearby enemy. CD: §e3s§7."; }
}
