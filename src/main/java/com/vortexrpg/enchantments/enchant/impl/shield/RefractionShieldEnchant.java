package com.vortexrpg.enchantments.enchant.impl.shield;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

/** Refraction (Shield): While blocking, reflect 8/12/18% of projectile damage as area damage. */
public class RefractionShieldEnchant extends VortexEnchant {
    private static final double[] RATIO = {0.08, 0.12, 0.18};

    public RefractionShieldEnchant() { super("refraction_shield", "Refraction", EnchantRarity.RARE, 3, List.of(ItemTarget.SHIELD)); }

    @Override
    public void onDamageTaken(EntityDamageByEntityEvent event, Player player, int level) {
        if (!isEnabled() || !player.isBlocking()) return;
        if (!(event.getDamager() instanceof org.bukkit.entity.Projectile)) return;
        double ratio = cfg("ratio", RATIO[level-1]);
        double aoe = event.getDamage() * ratio;
        int r = cfgi("radius", 4);
        player.getWorld().getNearbyLivingEntities(player.getLocation(), r, r, r,
            e -> !(e instanceof Player)).forEach(e -> e.damage(aoe, player));
    }

    @Override public String getDescription() { return "Blocks scatter projectile damage to nearby mobs."; }
    @Override public String getDescription(int level) {
        return "§7Blocking projectiles: §a" + (int)(RATIO[level-1]*100) + "§a%§7 reflected as AOE."; }
}
