package com.vortexrpg.enchantments.enchant.impl.shield;

import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 * Martyr — Shield (Epic, Max 1)
 * When a nearby ally would take fatal damage, redirect 50% of it to the bearer instead.
 */
public class MartyrEnchant extends VortexEnchant {

    public MartyrEnchant() {
        super("martyr", "Martyr", "shield");
    }

    @Override
    public String getTier() { return "EPIC"; }

    @Override
    public int getMaxLevel() { return 1; }

    @Override
    public String getDescription(int level) {
        return "When a nearby ally would receive fatal damage, you absorb §c50%§7 of it.";
    }

    /**
     * Called when THIS player is being checked as a potential Martyr for a nearby ally.
     * The EnchantmentListener must call this when any player is about to take lethal damage:
     * scan nearby players with Martyr shield enchant and transfer half the damage to them.
     *
     * The onDamageTaken hook here protects the martyr themselves from being loop-redirected.
     * Actual martyr transfer logic lives in the listener because it requires scanning nearby players.
     */
    @Override
    public void onDamageTaken(EntityDamageByEntityEvent event, Player player, int level) {
        // Martyr's own protection is passive — no self-reduction here.
        // The redirect logic is invoked externally by the listener when an ally is in danger.
    }

    /**
     * Executes the martyr redirect: called by listener when ally (victim) is about to die.
     * @param martyr   the player bearing this enchant
     * @param victim   the ally who would have died
     * @param damage   original damage dealt to victim
     * @param level    enchant level
     * @return damage redirected to martyr (remainder stays on victim)
     */
    public double performRedirect(Player martyr, Player victim, double damage, int level) {
        double radius = cfgd("radius", 8.0);
        if (martyr.getWorld() != victim.getWorld()) return 0;
        if (martyr.getLocation().distanceSquared(victim.getLocation()) > radius * radius) return 0;
        double ratio = cfgd("redirect_ratio", 0.50);
        double absorbed = damage * ratio;
        martyr.damage(absorbed);
        return absorbed;
    }
}
