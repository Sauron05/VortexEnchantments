package com.vortexrpg.enchantments.enchant;

import com.vortexrpg.enchantments.VortexEnchantments;
import com.vortexrpg.enchantments.util.ItemUtil;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Map;

/**
 * Master event router. Dispatches events to the appropriate enchantment hooks.
 */
public class EnchantListener implements Listener {

    private final VortexEnchantments plugin;
    private final EnchantManager manager;

    public EnchantListener(VortexEnchantments plugin) {
        this.plugin = plugin;
        this.manager = plugin.getEnchantManager();
        // Start passive tick task (every second)
        plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                for (ItemStack item : ItemUtil.getEquipped(player)) {
                    Map<VortexEnchant, Integer> enchants = manager.getEnchants(item);
                    for (Map.Entry<VortexEnchant, Integer> e : enchants.entrySet()) {
                        if (e.getKey().isEnabled()) {
                            e.getKey().tickPassive(player, e.getValue());
                        }
                    }
                }
            }
        }, 20L, 20L);
    }

    // ─── Combat: attacks ─────────────────────────────────────────────────────

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        // Case 1: Player melee attacking living entity
        if (event.getDamager() instanceof Player attacker && event.getEntity() instanceof LivingEntity victim) {
            ItemStack weapon = attacker.getInventory().getItemInMainHand();
            if (!ItemUtil.isAir(weapon)) {
                Map<VortexEnchant, Integer> enchants = manager.getEnchants(weapon);
                for (Map.Entry<VortexEnchant, Integer> e : enchants.entrySet()) {
                    if (e.getKey().isEnabled()) {
                        e.getKey().onAttack(event, attacker, victim, e.getValue());
                    }
                }
            }
            // Also fire armor enchants (onDamaged for the VICTIM if they're a player)
            // handled below
        }

        // Case 2: Player is being damaged by an entity
        if (event.getEntity() instanceof Player victim) {
            Entity attacker = event.getDamager();
            // Check all equipped armor/off-hand for onDamaged and onDamageTaken(ByEntity)
            for (ItemStack item : ItemUtil.getEquipped(victim)) {
                Map<VortexEnchant, Integer> enchants = manager.getEnchants(item);
                for (Map.Entry<VortexEnchant, Integer> e : enchants.entrySet()) {
                    if (e.getKey().isEnabled()) {
                        e.getKey().onDamaged(event, victim, attacker, e.getValue());
                        e.getKey().onDamageTaken(event, victim, e.getValue());
                    }
                }
            }
        }

        // Case 3: Projectile damage (bow/crossbow/trident arrow)
        if (event.getDamager() instanceof Projectile proj && event.getEntity() instanceof LivingEntity victim) {
            if (proj.getShooter() instanceof Player shooter) {
                ItemStack weapon = shooter.getInventory().getItemInMainHand();
                // Check if the projectile was tagged with the weapon enchants
                // (metadata reserved for future per-projectile enchant tracking)
                // Fire arrow-hit-entity enchants from main or tracked weapon
                if (!ItemUtil.isAir(weapon)) {
                    Map<VortexEnchant, Integer> enchants = manager.getEnchants(weapon);
                    for (Map.Entry<VortexEnchant, Integer> e : enchants.entrySet()) {
                        if (e.getKey().isEnabled()) {
                            e.getKey().onArrowHitEntity(event, shooter, victim, e.getValue());
                        }
                    }
                }
            }
        }
    }

    // ─── Combat: kills ───────────────────────────────────────────────────────

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity killed = event.getEntity();
        Player killer = killed.getKiller();
        if (killer == null) return;

        ItemStack weapon = killer.getInventory().getItemInMainHand();
        if (!ItemUtil.isAir(weapon)) {
            Map<VortexEnchant, Integer> enchants = manager.getEnchants(weapon);
            for (Map.Entry<VortexEnchant, Integer> e : enchants.entrySet()) {
                if (e.getKey().isEnabled()) {
                    e.getKey().onKill(event, killer, killed, e.getValue());
                }
            }
        }
    }

    // ─── Block break ────────────────────────────────────────────────────────

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (ItemUtil.isAir(item)) return;

        Map<VortexEnchant, Integer> enchants = manager.getEnchants(item);
        for (Map.Entry<VortexEnchant, Integer> e : enchants.entrySet()) {
            if (e.getKey().isEnabled()) {
                e.getKey().onBlockBreak(event, player, event.getBlock(), e.getValue());
            }
        }
    }

    // ─── Interact (right/left click) ────────────────────────────────────────

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (item == null) item = player.getInventory().getItemInMainHand();
        if (ItemUtil.isAir(item)) return;

        final ItemStack finalItem = item;
        Map<VortexEnchant, Integer> enchants = manager.getEnchants(item);
        for (Map.Entry<VortexEnchant, Integer> e : enchants.entrySet()) {
            if (e.getKey().isEnabled()) {
                e.getKey().onInteract(event, player, finalItem, e.getValue());
            }
        }
    }

    // ─── Projectile launch ──────────────────────────────────────────────────

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onShoot(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player shooter)) return;
        ItemStack bow = event.getBow();
        if (bow == null) bow = shooter.getInventory().getItemInMainHand();
        if (ItemUtil.isAir(bow)) return;

        Map<VortexEnchant, Integer> enchants = manager.getEnchants(bow);
        for (Map.Entry<VortexEnchant, Integer> e : enchants.entrySet()) {
            if (e.getKey().isEnabled()) {
                e.getKey().onShoot(event, shooter, e.getValue());
            }
        }

        // Tag projectile with shooter UUID for later lookup
        if (event.getProjectile() instanceof Projectile proj) {
            proj.setMetadata("ve_shooter", new FixedMetadataValue(plugin, shooter.getUniqueId().toString()));
        }
    }

    // ─── Projectile hit block ────────────────────────────────────────────────

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onProjectileHit(ProjectileHitEvent event) {
        Projectile proj = event.getEntity();
        if (!(proj.getShooter() instanceof Player shooter)) return;

        ItemStack weapon = shooter.getInventory().getItemInMainHand();
        if (ItemUtil.isAir(weapon)) return;

        Map<VortexEnchant, Integer> enchants = manager.getEnchants(weapon);
        for (Map.Entry<VortexEnchant, Integer> e : enchants.entrySet()) {
            if (e.getKey().isEnabled()) {
                e.getKey().onArrowHitBlock(event, shooter, e.getValue());
            }
        }
    }

    // ─── Fishing ─────────────────────────────────────────────────────────────

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerFish(PlayerFishEvent event) {
        Player player = event.getPlayer();
        ItemStack rod = player.getInventory().getItemInMainHand();
        if (ItemUtil.isAir(rod)) return;

        Map<VortexEnchant, Integer> enchants = manager.getEnchants(rod);
        for (Map.Entry<VortexEnchant, Integer> e : enchants.entrySet()) {
            if (e.getKey().isEnabled()) {
                e.getKey().onFish(event, player, e.getValue());
            }
        }
    }

    // ─── Player move ─────────────────────────────────────────────────────────

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        // Only fire if player actually moved (not just looked)
        if (!event.hasChangedBlock() && event.getFrom().getX() == event.getTo().getX()
            && event.getFrom().getZ() == event.getTo().getZ()) return;

        Player player = event.getPlayer();
        for (ItemStack item : ItemUtil.getEquipped(player)) {
            Map<VortexEnchant, Integer> enchants = manager.getEnchants(item);
            for (Map.Entry<VortexEnchant, Integer> e : enchants.entrySet()) {
                if (e.getKey().isEnabled()) {
                    e.getKey().onMove(event, player, e.getValue());
                }
            }
        }
    }

    // ─── Sneak ───────────────────────────────────────────────────────────────

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onToggleSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        for (ItemStack item : ItemUtil.getEquipped(player)) {
            Map<VortexEnchant, Integer> enchants = manager.getEnchants(item);
            for (Map.Entry<VortexEnchant, Integer> e : enchants.entrySet()) {
                if (e.getKey().isEnabled()) {
                    e.getKey().onToggleSneak(event, player, e.getValue());
                }
            }
        }
    }

    // ─── Harvest ─────────────────────────────────────────────────────────────

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onHarvest(PlayerHarvestBlockEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (ItemUtil.isAir(item)) return;

        Map<VortexEnchant, Integer> enchants = manager.getEnchants(item);
        for (Map.Entry<VortexEnchant, Integer> e : enchants.entrySet()) {
            if (e.getKey().isEnabled()) {
                e.getKey().onHarvest(event, player, e.getValue());
            }
        }
    }

    // ─── General damage (non-entity) ────────────────────────────────────────

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (event instanceof EntityDamageByEntityEvent) return; // handled above

        for (ItemStack item : ItemUtil.getEquipped(player)) {
            Map<VortexEnchant, Integer> enchants = manager.getEnchants(item);
            for (Map.Entry<VortexEnchant, Integer> e : enchants.entrySet()) {
                if (e.getKey().isEnabled()) {
                    e.getKey().onDamageTaken(event, player, e.getValue());
                }
            }
        }
    }

    // ─── Respawn ─────────────────────────────────────────────────────────────

    @EventHandler(priority = EventPriority.MONITOR)
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            for (ItemStack item : ItemUtil.getEquipped(player)) {
                Map<VortexEnchant, Integer> enchants = manager.getEnchants(item);
                for (Map.Entry<VortexEnchant, Integer> e : enchants.entrySet()) {
                    if (e.getKey().isEnabled()) {
                        e.getKey().onRespawn(event, player, e.getValue());
                    }
                }
            }
        }, 1L);
    }

    // ─── Passive tick task ───────────────────────────────────────────────────
    // (Started in constructor)

    // ─── Player quit cleanup ─────────────────────────────────────────────────

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        plugin.getPlayerDataManager().cleanupPlayer(event.getPlayer().getUniqueId());
    }
}
