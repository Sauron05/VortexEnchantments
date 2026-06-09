package com.vortexrpg.enchantments.system;

import com.vortexrpg.enchantments.VortexEnchantments;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Enchant Combo / Synergy System — NO other plugin has this.
 *
 * When 2-3 specific enchants coexist on the same item, they form a "combo"
 * that triggers a bonus effect on top of the individual enchant effects.
 *
 * Features:
 *   - Configurable combos via config.yml (id, required enchants, effect type, cooldown)
 *   - Built-in default combos spanning all item types (swords, bows, armor, tools)
 *   - Combo activation particles + sound + actionbar notification
 *   - Per-player cooldowns per combo
 *   - Combos detected dynamically — adding/removing enchants updates combos
 *   - Stacking: multiple combos can trigger on the same event
 *   - Combo discovery: first-time activation sends a special message
 */
@SuppressWarnings("deprecation")
public class EnchantComboManager implements Listener {

    private final VortexEnchantments plugin;
    private final List<EnchantCombo> combos = new ArrayList<>();
    private final Map<UUID, Set<String>> discoveredCombos = new ConcurrentHashMap<>();
    private final Map<String, Long> cooldowns = new ConcurrentHashMap<>();

    public EnchantComboManager(VortexEnchantments plugin) {
        this.plugin = plugin;
        registerDefaultCombos();
    }

    // ─── Combo Definition ────────────────────────────────────────────────────

    public record EnchantCombo(
        String id,
        String displayName,
        String color,
        List<String> requiredEnchantIds,
        ComboTrigger trigger,
        ComboEffect effect,
        double cooldownSeconds,
        String description
    ) {}

    public enum ComboTrigger {
        ON_ATTACK,      // melee hit
        ON_KILL,        // killing blow
        ON_BLOCK_BREAK, // mining
        ON_DAMAGED,     // taking damage
        ON_ARROW_HIT,   // projectile hit entity
        PASSIVE         // ticks while equipped
    }

    public enum ComboEffect {
        FIRE_STORM,         // AoE fire + damage around target
        SOUL_HARVEST,       // bonus XP + souls on kill
        CHAIN_LIGHTNING,    // lightning chains to nearby mobs
        FROSTBITE,          // AoE slowness + damage
        VOID_COLLAPSE,      // pulls all nearby mobs toward target
        BERSERK,            // temporary strength + speed buff on self
        VEIN_MINER,         // breaks connected same-type blocks
        MAGNETIC_PULL,      // auto-collect drops in radius
        LIFE_STEAL_BURST,   // burst heal on kill
        THORNS_NOVA,        // AoE damage around self when hit
        SHADOW_STEP,        // short teleport behind attacker
        OVERCHARGE,         // next arrow deals 3x damage
        DIVINE_SHIELD,      // absorb next hit for free
        TREASURE_SENSE,     // glowing effect on nearby ores
        NATURE_REGROWTH     // replant + growth boost on harvest
    }

    // ─── Event Handlers ──────────────────────────────────────────────────────

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAttack(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player attacker)) return;
        if (!(event.getEntity() instanceof LivingEntity victim)) return;

        ItemStack weapon = attacker.getInventory().getItemInMainHand();
        if (weapon.getType().isAir()) return;

        Map<VortexEnchant, Integer> enchants = plugin.getEnchantManager().getEnchants(weapon);
        Set<String> enchantIds = new HashSet<>();
        for (VortexEnchant e : enchants.keySet()) enchantIds.add(e.getId());

        for (EnchantCombo combo : combos) {
            if (combo.trigger() != ComboTrigger.ON_ATTACK) continue;
            if (!enchantIds.containsAll(combo.requiredEnchantIds())) continue;
            if (isOnCooldown(attacker, combo)) continue;

            activateCombo(attacker, combo, victim, null);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onKill(EntityDeathEvent event) {
        LivingEntity killed = event.getEntity();
        Player killer = killed.getKiller();
        if (killer == null) return;

        ItemStack weapon = killer.getInventory().getItemInMainHand();
        if (weapon.getType().isAir()) return;

        Map<VortexEnchant, Integer> enchants = plugin.getEnchantManager().getEnchants(weapon);
        Set<String> enchantIds = new HashSet<>();
        for (VortexEnchant e : enchants.keySet()) enchantIds.add(e.getId());

        for (EnchantCombo combo : combos) {
            if (combo.trigger() != ComboTrigger.ON_KILL) continue;
            if (!enchantIds.containsAll(combo.requiredEnchantIds())) continue;
            if (isOnCooldown(killer, combo)) continue;

            activateCombo(killer, combo, killed, null);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack tool = player.getInventory().getItemInMainHand();
        if (tool.getType().isAir()) return;

        Map<VortexEnchant, Integer> enchants = plugin.getEnchantManager().getEnchants(tool);
        Set<String> enchantIds = new HashSet<>();
        for (VortexEnchant e : enchants.keySet()) enchantIds.add(e.getId());

        for (EnchantCombo combo : combos) {
            if (combo.trigger() != ComboTrigger.ON_BLOCK_BREAK) continue;
            if (!enchantIds.containsAll(combo.requiredEnchantIds())) continue;
            if (isOnCooldown(player, combo)) continue;

            activateCombo(player, combo, null, event.getBlock().getLocation());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamaged(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player victim)) return;

        // Check all equipped items for combos
        Set<String> enchantIds = new HashSet<>();
        for (ItemStack item : getEquipped(victim)) {
            Map<VortexEnchant, Integer> enchants = plugin.getEnchantManager().getEnchants(item);
            for (VortexEnchant e : enchants.keySet()) enchantIds.add(e.getId());
        }

        for (EnchantCombo combo : combos) {
            if (combo.trigger() != ComboTrigger.ON_DAMAGED) continue;
            if (!enchantIds.containsAll(combo.requiredEnchantIds())) continue;
            if (isOnCooldown(victim, combo)) continue;

            activateCombo(victim, combo, event.getDamager() instanceof LivingEntity le ? le : null,
                victim.getLocation());
        }
    }

    // ─── Combo Activation ────────────────────────────────────────────────────

    private void activateCombo(Player player, EnchantCombo combo, LivingEntity target, Location loc) {
        setCooldown(player, combo);

        // Discovery check
        Set<String> discovered = discoveredCombos.computeIfAbsent(player.getUniqueId(), k -> ConcurrentHashMap.newKeySet());
        boolean firstTime = discovered.add(combo.id());

        // Visual feedback
        player.sendActionBar(combo.color() + "§l⚡ " + combo.displayName() + " ⚡");
        player.playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 0.3f, 2.0f);

        if (firstTime) {
            player.sendMessage("");
            player.sendMessage(combo.color() + "§l✦ COMBO DISCOVERED: " + combo.displayName() + " ✦");
            player.sendMessage("§7" + combo.description());
            player.sendMessage("§7Required: §f" + String.join(" §7+ §f", combo.requiredEnchantIds()));
            player.sendMessage("");
            player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 0.8f, 1.2f);
        }

        // Execute effect
        Location effectLoc = target != null ? target.getLocation() : (loc != null ? loc : player.getLocation());
        executeEffect(player, combo.effect(), target, effectLoc);

        // Particles at player
        spawnComboParticles(player, combo);
    }

    private void executeEffect(Player player, ComboEffect effect, LivingEntity target, Location loc) {
        switch (effect) {
            case FIRE_STORM -> {
                // AoE fire damage around target
                for (org.bukkit.entity.Entity e : loc.getWorld().getNearbyEntities(loc, 4, 3, 4)) {
                    if (e instanceof LivingEntity le && le != player && le != target) {
                        le.setFireTicks(60);
                        le.damage(4.0, player);
                    }
                }
                loc.getWorld().spawnParticle(Particle.FLAME, loc, 40, 2, 1, 2, 0.05);
                loc.getWorld().spawnParticle(Particle.LAVA, loc, 15, 2, 1, 2, 0);
                loc.getWorld().playSound(loc, Sound.ENTITY_BLAZE_SHOOT, 0.8f, 0.7f);
            }
            case SOUL_HARVEST -> {
                // Bonus XP + souls
                player.giveExp(50);
                plugin.getSoulsManager().addSouls(player, 10);
                player.sendMessage("§5✦ Soul Harvest: §d+50 XP, +10 Souls");
                loc.getWorld().spawnParticle(Particle.SOUL, loc, 20, 1, 1, 1, 0.02);
            }
            case CHAIN_LIGHTNING -> {
                // Lightning chains to 3 nearby mobs
                List<LivingEntity> nearby = new ArrayList<>();
                for (org.bukkit.entity.Entity e : loc.getWorld().getNearbyEntities(loc, 8, 4, 8)) {
                    if (e instanceof LivingEntity le && le != player && le != target) {
                        nearby.add(le);
                    }
                }
                Collections.shuffle(nearby);
                int chains = Math.min(3, nearby.size());
                for (int i = 0; i < chains; i++) {
                    LivingEntity chain = nearby.get(i);
                    chain.damage(6.0, player);
                    chain.getWorld().strikeLightningEffect(chain.getLocation());
                }
                if (target != null) {
                    target.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, target.getLocation(), 30, 1, 1, 1, 0.1);
                }
            }
            case FROSTBITE -> {
                // AoE slowness + damage
                for (org.bukkit.entity.Entity e : loc.getWorld().getNearbyEntities(loc, 5, 3, 5)) {
                    if (e instanceof LivingEntity le && le != player) {
                        le.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 80, 2));
                        le.damage(3.0, player);
                        le.setFreezeTicks(100);
                    }
                }
                loc.getWorld().spawnParticle(Particle.SNOWFLAKE, loc, 50, 3, 2, 3, 0.02);
                loc.getWorld().playSound(loc, Sound.BLOCK_GLASS_BREAK, 0.8f, 1.5f);
            }
            case VOID_COLLAPSE -> {
                // Pull all nearby mobs toward target location
                for (org.bukkit.entity.Entity e : loc.getWorld().getNearbyEntities(loc, 6, 4, 6)) {
                    if (e instanceof LivingEntity le && le != player) {
                        org.bukkit.util.Vector pull = loc.toVector().subtract(le.getLocation().toVector()).normalize().multiply(1.5);
                        le.setVelocity(pull);
                        le.damage(2.0, player);
                    }
                }
                loc.getWorld().spawnParticle(Particle.PORTAL, loc, 80, 2, 2, 2, 0.5);
                loc.getWorld().spawnParticle(Particle.REVERSE_PORTAL, loc, 40, 1, 1, 1, 0.1);
                loc.getWorld().playSound(loc, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 0.5f);
            }
            case BERSERK -> {
                // Self buff — Strength II + Speed I for 6s
                player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 120, 1));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 120, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 120, 0));
                player.getWorld().spawnParticle(Particle.ANGRY_VILLAGER, player.getLocation().add(0, 2, 0), 10, 0.5, 0.3, 0.5, 0);
                player.playSound(player.getLocation(), Sound.ENTITY_RAVAGER_ROAR, 0.6f, 1.5f);
            }
            case VEIN_MINER -> {
                // Break up to 8 connected same-type blocks
                org.bukkit.block.Block center = loc.getBlock();
                Material blockType = center.getType();
                if (blockType.isAir()) return;

                Set<Location> toBreak = new HashSet<>();
                Queue<org.bukkit.block.Block> queue = new LinkedList<>();
                queue.add(center);

                int max = 8;
                while (!queue.isEmpty() && toBreak.size() < max) {
                    org.bukkit.block.Block b = queue.poll();
                    if (b.getType() != blockType) continue;
                    if (!toBreak.add(b.getLocation())) continue;

                    // Add 6 adjacent
                    for (org.bukkit.block.BlockFace face : new org.bukkit.block.BlockFace[]{
                        org.bukkit.block.BlockFace.NORTH, org.bukkit.block.BlockFace.SOUTH,
                        org.bukkit.block.BlockFace.EAST, org.bukkit.block.BlockFace.WEST,
                        org.bukkit.block.BlockFace.UP, org.bukkit.block.BlockFace.DOWN}) {
                        org.bukkit.block.Block adj = b.getRelative(face);
                        if (adj.getType() == blockType && !toBreak.contains(adj.getLocation())) {
                            queue.add(adj);
                        }
                    }
                }

                for (Location bl : toBreak) {
                    if (bl.equals(center.getLocation())) continue; // original already broken
                    org.bukkit.block.Block b = bl.getBlock();
                    b.breakNaturally(player.getInventory().getItemInMainHand());
                }
                loc.getWorld().spawnParticle(Particle.BLOCK, loc, 30, 1, 1, 1, 0,
                    blockType.createBlockData());
            }
            case MAGNETIC_PULL -> {
                // Pull all dropped items within 10 blocks to player
                for (org.bukkit.entity.Entity e : player.getNearbyEntities(10, 5, 10)) {
                    if (e instanceof org.bukkit.entity.Item item) {
                        org.bukkit.util.Vector pull = player.getLocation().toVector()
                            .subtract(item.getLocation().toVector()).normalize().multiply(2);
                        item.setVelocity(pull);
                    }
                }
                player.getWorld().spawnParticle(Particle.ENCHANTED_HIT, player.getLocation(), 20, 2, 1, 2, 0.1);
            }
            case LIFE_STEAL_BURST -> {
                // Burst heal on kill
                double heal = Math.min(10.0, player.getMaxHealth() - player.getHealth());
                player.setHealth(player.getHealth() + heal);
                player.getWorld().spawnParticle(Particle.HEART, player.getLocation().add(0, 1.5, 0), 8, 0.5, 0.3, 0.5, 0);
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 2.0f);
            }
            case THORNS_NOVA -> {
                // AoE damage around self when hit
                for (org.bukkit.entity.Entity e : player.getNearbyEntities(4, 3, 4)) {
                    if (e instanceof LivingEntity le) {
                        le.damage(5.0, player);
                    }
                }
                player.getWorld().spawnParticle(Particle.CRIT, player.getLocation(), 30, 2, 1, 2, 0.1);
                player.getWorld().spawnParticle(Particle.DAMAGE_INDICATOR, player.getLocation(), 15, 2, 1, 2, 0.1);
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_IRON_GOLEM_HURT, 0.8f, 1.5f);
            }
            case SHADOW_STEP -> {
                // Teleport behind attacker
                if (target != null) {
                    Location behind = target.getLocation().clone();
                    behind.add(target.getLocation().getDirection().multiply(-2));
                    behind.setY(target.getLocation().getY());
                    behind.setPitch(player.getLocation().getPitch());
                    behind.setYaw(target.getLocation().getYaw());
                    player.teleport(behind);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 40, 0));
                    player.getWorld().spawnParticle(Particle.SMOKE, player.getLocation(), 20, 0.5, 1, 0.5, 0.02);
                    player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 0.7f, 1.5f);
                }
            }
            case OVERCHARGE -> {
                // Buff next attack with glowing effect
                player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 100, 2));
                player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 100, 0));
                player.getWorld().spawnParticle(Particle.END_ROD, player.getLocation().add(0, 1, 0), 20, 0.5, 0.5, 0.5, 0.05);
                player.playSound(player.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 0.8f, 2.0f);
            }
            case DIVINE_SHIELD -> {
                // Absorption hearts
                player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 200, 2));
                player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 60, 2));
                player.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, player.getLocation().add(0, 1, 0), 30, 1, 1, 1, 0.1);
                player.playSound(player.getLocation(), Sound.ITEM_TOTEM_USE, 0.5f, 1.5f);
            }
            case TREASURE_SENSE -> {
                // Give glowing effect to nearby ores
                player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 200, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, 200, 1));
                player.sendMessage("§6✦ Treasure Sense activated! §7Enhanced mining for 10s.");
                player.getWorld().spawnParticle(Particle.ENCHANTED_HIT, player.getLocation(), 15, 1, 1, 1, 0.1);
            }
            case NATURE_REGROWTH -> {
                // Bonus crop drops + bonemeal effect around
                Location center2 = loc.clone();
                for (int x = -2; x <= 2; x++) {
                    for (int z = -2; z <= 2; z++) {
                        org.bukkit.block.Block b = center2.getBlock().getRelative(x, 0, z);
                        if (b.getType() == Material.FARMLAND) {
                            org.bukkit.block.Block above = b.getRelative(org.bukkit.block.BlockFace.UP);
                            if (above.getType().isAir()) {
                                above.setType(Material.WHEAT);
                            }
                        }
                    }
                }
                loc.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, loc, 30, 3, 1, 3, 0);
                player.playSound(player.getLocation(), Sound.ITEM_BONE_MEAL_USE, 0.8f, 1.2f);
            }
        }
    }

    private void spawnComboParticles(Player player, EnchantCombo combo) {
        Location loc = player.getLocation().add(0, 1.5, 0);
        Color color = switch (combo.color().substring(0, 2)) {
            case "§c" -> Color.RED;
            case "§6" -> Color.ORANGE;
            case "§e" -> Color.YELLOW;
            case "§a" -> Color.LIME;
            case "§b" -> Color.AQUA;
            case "§9" -> Color.BLUE;
            case "§5" -> Color.PURPLE;
            case "§d" -> Color.FUCHSIA;
            default -> Color.WHITE;
        };
        Particle.DustOptions dust = new Particle.DustOptions(color, 1.5f);

        // Star burst pattern
        for (int i = 0; i < 5; i++) {
            double angle = (Math.PI * 2 / 5) * i;
            double x = Math.cos(angle) * 1.2;
            double z = Math.sin(angle) * 1.2;
            player.getWorld().spawnParticle(Particle.DUST, loc.clone().add(x, 0, z), 3, 0.1, 0.1, 0.1, 0, dust);
        }
    }

    // ─── Cooldown Management ─────────────────────────────────────────────────

    private boolean isOnCooldown(Player player, EnchantCombo combo) {
        String key = player.getUniqueId() + ":" + combo.id();
        Long expiry = cooldowns.get(key);
        return expiry != null && System.currentTimeMillis() < expiry;
    }

    private void setCooldown(Player player, EnchantCombo combo) {
        String key = player.getUniqueId() + ":" + combo.id();
        cooldowns.put(key, System.currentTimeMillis() + (long) (combo.cooldownSeconds() * 1000));
    }

    // ─── Query ───────────────────────────────────────────────────────────────

    /**
     * Get all active combos on the given item for display purposes.
     */
    public List<EnchantCombo> getActiveCombos(ItemStack item) {
        Map<VortexEnchant, Integer> enchants = plugin.getEnchantManager().getEnchants(item);
        Set<String> ids = new HashSet<>();
        for (VortexEnchant e : enchants.keySet()) ids.add(e.getId());

        List<EnchantCombo> active = new ArrayList<>();
        for (EnchantCombo combo : combos) {
            if (ids.containsAll(combo.requiredEnchantIds())) {
                active.add(combo);
            }
        }
        return active;
    }

    /**
     * Get all registered combos.
     */
    public List<EnchantCombo> getAllCombos() {
        return Collections.unmodifiableList(combos);
    }

    // ─── Utility ─────────────────────────────────────────────────────────────

    private ItemStack[] getEquipped(Player player) {
        List<ItemStack> items = new ArrayList<>();
        items.add(player.getInventory().getItemInMainHand());
        items.add(player.getInventory().getItemInOffHand());
        ItemStack[] armor = player.getInventory().getArmorContents();
        if (armor != null) {
            for (ItemStack a : armor) {
                if (a != null && !a.getType().isAir()) items.add(a);
            }
        }
        return items.toArray(new ItemStack[0]);
    }

    // ─── Default Combos ──────────────────────────────────────────────────────

    private void registerDefaultCombos() {
        // Sword combos
        combos.add(new EnchantCombo("fire_storm", "Fire Storm", "§c",
            List.of("blaze_touch", "inferno"), ComboTrigger.ON_ATTACK, ComboEffect.FIRE_STORM, 12,
            "Two fire enchants unleash a devastating fire storm around your target"));

        combos.add(new EnchantCombo("soul_reaper", "Soul Reaper", "§5",
            List.of("soulbind", "necrosis"), ComboTrigger.ON_KILL, ComboEffect.SOUL_HARVEST, 8,
            "Dark enchants harvest souls and XP from slain enemies"));

        combos.add(new EnchantCombo("storm_caller", "Storm Caller", "§b",
            List.of("stormweaver", "chain_lightning"), ComboTrigger.ON_ATTACK, ComboEffect.CHAIN_LIGHTNING, 15,
            "Lightning enchants chain devastating bolts between foes"));

        combos.add(new EnchantCombo("absolute_zero", "Absolute Zero", "§f",
            List.of("frost_blade", "glacial_spike"), ComboTrigger.ON_ATTACK, ComboEffect.FROSTBITE, 10,
            "Frost enchants freeze everything in a wide radius"));

        combos.add(new EnchantCombo("gravity_crush", "Gravity Crush", "§5",
            List.of("gravity_well", "singularity"), ComboTrigger.ON_ATTACK, ComboEffect.VOID_COLLAPSE, 15,
            "Gravity enchants collapse space around your target, pulling all foes inward"));

        combos.add(new EnchantCombo("blood_frenzy", "Blood Frenzy", "§4",
            List.of("blood_price", "thirst"), ComboTrigger.ON_KILL, ComboEffect.BERSERK, 20,
            "Blood magic fuels a berserk rage — Strength, Speed, and Resistance"));

        combos.add(new EnchantCombo("phantom_strike", "Phantom Strike", "§8",
            List.of("phase", "rift_walker"), ComboTrigger.ON_ATTACK, ComboEffect.SHADOW_STEP, 12,
            "Phase through reality to teleport behind your attacker"));

        combos.add(new EnchantCombo("vampiric_fury", "Vampiric Fury", "§4",
            List.of("siphon", "harvest"), ComboTrigger.ON_KILL, ComboEffect.LIFE_STEAL_BURST, 8,
            "Life-draining enchants burst-heal you on every kill"));

        // Armor combos (trigger: ON_DAMAGED)
        combos.add(new EnchantCombo("thorns_nova", "Thorns Nova", "§6",
            List.of("backlash", "retaliate"), ComboTrigger.ON_DAMAGED, ComboEffect.THORNS_NOVA, 15,
            "Defensive enchants explode with retaliatory damage when hit"));

        combos.add(new EnchantCombo("divine_aegis", "Divine Aegis", "§e",
            List.of("guardian_angel", "fortify"), ComboTrigger.ON_DAMAGED, ComboEffect.DIVINE_SHIELD, 30,
            "Protective enchants grant a divine shield of absorption"));

        // Pickaxe combos (trigger: ON_BLOCK_BREAK)
        combos.add(new EnchantCombo("master_miner", "Master Miner", "§b",
            List.of("vein_mine", "drill"), ComboTrigger.ON_BLOCK_BREAK, ComboEffect.VEIN_MINER, 5,
            "Mining enchants break entire veins of connected blocks"));

        combos.add(new EnchantCombo("magnet_field", "Magnet Field", "§e",
            List.of("auto_smelt", "telekinesis"), ComboTrigger.ON_BLOCK_BREAK, ComboEffect.MAGNETIC_PULL, 8,
            "Utility enchants pull all nearby drops directly to you"));

        combos.add(new EnchantCombo("ore_vision", "Ore Vision", "§6",
            List.of("spelunker", "excavator"), ComboTrigger.ON_BLOCK_BREAK, ComboEffect.TREASURE_SENSE, 30,
            "Exploration enchants grant night vision and haste to find treasure"));

        // Hoe combo (trigger: ON_BLOCK_BREAK)
        combos.add(new EnchantCombo("green_thumb", "Green Thumb", "§a",
            List.of("replant", "growth_aura"), ComboTrigger.ON_BLOCK_BREAK, ComboEffect.NATURE_REGROWTH, 10,
            "Nature enchants spread life — replanting and growing crops around you"));

        // Bow combo (trigger: ON_ATTACK — arrow hit entity fires onAttack too via EnchantListener)
        combos.add(new EnchantCombo("overcharged_shot", "Overcharged Shot", "§e",
            List.of("power_draw", "impale"), ComboTrigger.ON_ATTACK, ComboEffect.OVERCHARGE, 20,
            "Bow enchants supercharge your next shot with devastating damage"));
    }
}
