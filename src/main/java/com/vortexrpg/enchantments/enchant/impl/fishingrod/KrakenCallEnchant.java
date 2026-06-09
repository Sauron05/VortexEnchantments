package com.vortexrpg.enchantments.enchant.impl.fishingrod;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;
import net.kyori.adventure.text.Component;

import java.util.List;

/** KrakenCall: On treasure catch, summon a guardian that attacks nearby hostiles. */
public class KrakenCallEnchant extends VortexEnchant {

    public KrakenCallEnchant() { super("kraken_call", "Kraken Call", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.FISHING_ROD)); }

    @Override
    public void onFish(PlayerFishEvent event, Player player, int level) {
        if (!isEnabled()) return;
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;
        if (Math.random() >= cfgd("chance", 0.15 + level * 0.05)) return;
        if (isOnCooldown(player)) return;
        setCooldownSeconds(player, cfgi("cooldown", 30));
        Guardian guardian = player.getWorld().spawn(event.getHook().getLocation(), Guardian.class);
        guardian.customName(Component.text("Kraken Tentacle"));
        guardian.setCustomNameVisible(true);
        player.getWorld().spawnParticle(Particle.BUBBLE_COLUMN_UP, guardian.getLocation(), 30, 1, 1, 1, 0.1);
        player.getWorld().playSound(guardian.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_CURSE, 1.0f, 0.5f);
        // Target nearest hostile mob
        for (Entity e : guardian.getWorld().getNearbyEntities(guardian.getLocation(), 10, 10, 10)) {
            if (e instanceof LivingEntity le && !(e instanceof Player) && le != guardian) {
                guardian.setTarget(le);
                break;
            }
        }
        // Despawn after 5 seconds
        plugin.getServer().getScheduler().runTaskLater(plugin, guardian::remove, cfgi("duration", 5) * 20L);
    }

    @Override public String getDescription() { return "Summon a guardian to fight for you."; }
    @Override public String getDescription(int level) {
        return "§7On catch, chance to summon a §bGuardian§7 that attacks nearby hostiles for §e5s§7."; }
}
