package com.vortexrpg.enchantments;

import com.vortexrpg.enchantments.api.VortexEnchantAPI;
import com.vortexrpg.enchantments.command.VortexCommand;
import com.vortexrpg.enchantments.config.ConfigManager;
import com.vortexrpg.enchantments.cooldown.CooldownManager;
import com.vortexrpg.enchantments.data.PlayerDataManager;
import com.vortexrpg.enchantments.enchant.EnchantListener;
import com.vortexrpg.enchantments.enchant.EnchantManager;
import com.vortexrpg.enchantments.enchant.SuccessRateManager;
import com.vortexrpg.enchantments.gui.*;
import com.vortexrpg.enchantments.hook.VaultHook;
import com.vortexrpg.enchantments.hook.VortexPlaceholders;
import com.vortexrpg.enchantments.item.DustItem;
import com.vortexrpg.enchantments.item.ExtractorScroll;
import com.vortexrpg.enchantments.item.HolyWhiteScroll;
import com.vortexrpg.enchantments.item.MysteryScroll;
import com.vortexrpg.enchantments.item.ProtectionScrolls;
import com.vortexrpg.enchantments.item.RandomizationScroll;
import com.vortexrpg.enchantments.item.SlotIncreaser;
import com.vortexrpg.enchantments.item.TransmogScroll;
import com.vortexrpg.enchantments.listener.*;
import com.vortexrpg.enchantments.system.*;
import com.vortexrpg.enchantments.util.SchedulerUtil;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class VortexEnchantments extends JavaPlugin {

    private static VortexEnchantments instance;
    private EnchantManager enchantManager;
    private CooldownManager cooldownManager;
    private PlayerDataManager playerDataManager;
    private ConfigManager configManager;
    private SuccessRateManager successRateManager;
    private EnchantBrowserGUI enchantBrowserGUI;
    private VortexEnchantAPI api;
    private AuraManager auraManager;
    private MobDropListener mobDropListener;
    private MysteryScroll mysteryScroll;
    private MysteryScrollGUI mysteryScrollGUI;
    private ExtractorScroll extractorScroll;
    private ExtractorGUI extractorGUI;
    private EnchantForgeGUI enchantForgeGUI;
    private AdminGUI adminGUI;
    private TinkererGUI tinkererGUI;
    private AlchemistGUI alchemistGUI;
    private SoulsShopGUI soulsShopGUI;
    private DustItem dustItem;
    private ProtectionScrolls protectionScrolls;
    private SlotIncreaser slotIncreaser;
    private TransmogScroll transmogScroll;
    private RandomizationScroll randomizationScroll;
    private HolyWhiteScroll holyWhiteScroll;
    private VaultHook vaultHook;
    private SoulsManager soulsManager;
    private ArmorSetManager armorSetManager;
    private WorldManager worldManager;
    private UpdateChecker updateChecker;
    private EnchantShopGUI enchantShopGUI;
    private EnchantLimitManager enchantLimitManager;
    private EnchantComboManager enchantComboManager;
    private EnchantEvolutionManager enchantEvolutionManager;
    private EnchantParticleManager enchantParticleManager;

    @Override
    public void onEnable() {
        instance = this;

        // Config first
        saveDefaultConfig();
        this.configManager = new ConfigManager(this);

        // Core managers
        this.cooldownManager = new CooldownManager(this);
        this.playerDataManager = new PlayerDataManager(this);
        this.enchantManager = new EnchantManager(this);
        this.successRateManager = new SuccessRateManager(this);
        this.enchantBrowserGUI = new EnchantBrowserGUI(this);
        this.api = new VortexEnchantAPI(this);

        // Items
        this.mysteryScroll = new MysteryScroll(this);
        this.extractorScroll = new ExtractorScroll(this);
        this.dustItem = new DustItem(this);
        this.protectionScrolls = new ProtectionScrolls(this);
        this.slotIncreaser = new SlotIncreaser(this);
        this.transmogScroll = new TransmogScroll(this);
        this.randomizationScroll = new RandomizationScroll(this);
        this.holyWhiteScroll = new HolyWhiteScroll(this);

        // GUIs
        this.mysteryScrollGUI = new MysteryScrollGUI(this);
        this.extractorGUI = new ExtractorGUI(this);
        this.enchantForgeGUI = new EnchantForgeGUI(this);
        this.adminGUI = new AdminGUI(this);
        this.tinkererGUI = new TinkererGUI(this);
        this.alchemistGUI = new AlchemistGUI(this);
        this.enchantShopGUI = new EnchantShopGUI(this);

        // Systems
        this.soulsManager = new SoulsManager(this);
        this.armorSetManager = new ArmorSetManager(this);
        this.worldManager = new WorldManager(this);
        this.enchantLimitManager = new EnchantLimitManager(this);
        this.auraManager = new AuraManager(this);
        this.enchantComboManager = new EnchantComboManager(this);
        this.enchantEvolutionManager = new EnchantEvolutionManager(this);
        this.enchantParticleManager = new EnchantParticleManager(this);
        this.mobDropListener = new MobDropListener(this);

        // Hooks
        this.vaultHook = new VaultHook();
        vaultHook.setup();
        if (vaultHook.isEnabled()) {
            getLogger().info("Vault economy hooked successfully!");
        }

        // PlaceholderAPI
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new VortexPlaceholders(this).register();
            getLogger().info("PlaceholderAPI expansion registered!");
        }

        // Souls shop GUI listener
        this.soulsShopGUI = new SoulsShopGUI(this);

        // Register all enchantments
        enchantManager.registerAll();

        // Register event listeners
        registerListeners();

        // Start aura particle task
        auraManager.start();

        // Start enchant particle task
        enchantParticleManager.start();

        // Start armor set tick
        startArmorSetTick();

        // Commands
        VortexCommand cmd = new VortexCommand(this);
        var cmdObj = getCommand("ve");
        if (cmdObj != null) {
            cmdObj.setExecutor(cmd);
            cmdObj.setTabCompleter(cmd);
        }

        // bStats
        try {
            new Metrics(this, 23456);
        } catch (Exception ignored) {}

        // Update checker
        this.updateChecker = new UpdateChecker(this);
        updateChecker.checkAsync();

        getLogger().info("§5══════════════════════════════════════════════");
        getLogger().info("§d  VortexEnchantments §7v" + com.vortexrpg.enchantments.util.PluginCompat.version(this));
        getLogger().info("§7  " + enchantManager.getEnchantCount() + " enchantments loaded!");
        getLogger().info("§7  Author: §fEternalRealm™");
        getLogger().info("§7  Website: §bhttps://eternalrealm.uk");
        getLogger().info("§7  Discord: §bhttps://discord.gg/Tya84XrgSF");
        getLogger().info("§5══════════════════════════════════════════════");
    }

    @Override
    public void onDisable() {
        if (auraManager != null) auraManager.stop();
        if (enchantParticleManager != null) enchantParticleManager.stop();
        if (playerDataManager != null) playerDataManager.saveAll();
        getLogger().info("VortexEnchantments disabled.");
    }

    private void registerListeners() {
        var pm = getServer().getPluginManager();
        pm.registerEvents(new EnchantListener(this), this);
        pm.registerEvents(new CombatListener(this), this);
        pm.registerEvents(new MiningListener(this), this);
        pm.registerEvents(new FarmingListener(this), this);
        pm.registerEvents(new MovementListener(this), this);
        pm.registerEvents(new ProjectileListener(this), this);
        pm.registerEvents(new ShieldListener(this), this);
        pm.registerEvents(new FishingListener(this), this);
        pm.registerEvents(new AnvilListener(this), this);
        pm.registerEvents(new ScrollListener(this), this);
        pm.registerEvents(new EnchantTableListener(this), this);
        pm.registerEvents(new VillagerListener(this), this);
        pm.registerEvents(new LootTableListener(this), this);
        pm.registerEvents(new BookApplyListener(this), this);
        pm.registerEvents(mobDropListener, this);
        pm.registerEvents(enchantBrowserGUI, this);
        pm.registerEvents(mysteryScrollGUI, this);
        pm.registerEvents(extractorGUI, this);
        pm.registerEvents(enchantForgeGUI, this);
        pm.registerEvents(adminGUI, this);
        pm.registerEvents(tinkererGUI, this);
        pm.registerEvents(alchemistGUI, this);
        pm.registerEvents(soulsManager, this);
        pm.registerEvents(soulsShopGUI, this);
        pm.registerEvents(enchantShopGUI, this);
        pm.registerEvents(enchantComboManager, this);
    }

    private void startArmorSetTick() {
        SchedulerUtil.runGlobalTimer(this, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                armorSetManager.tickPlayer(player);
            }
        }, 20L, 80L); // every 4 seconds
    }

    // ─── Getters ─────────────────────────────────────────────────────────────

    public static VortexEnchantments getInstance() { return instance; }
    public EnchantManager getEnchantManager() { return enchantManager; }
    public CooldownManager getCooldownManager() { return cooldownManager; }
    public PlayerDataManager getPlayerDataManager() { return playerDataManager; }
    public ConfigManager getConfigManager() { return configManager; }
    public SuccessRateManager getSuccessRateManager() { return successRateManager; }
    public EnchantBrowserGUI getEnchantBrowserGUI() { return enchantBrowserGUI; }
    public VortexEnchantAPI getApi() { return api; }
    public AuraManager getAuraManager() { return auraManager; }
    public MobDropListener getMobDropListener() { return mobDropListener; }
    public MysteryScroll getMysteryScroll() { return mysteryScroll; }
    public MysteryScrollGUI getMysteryScrollGUI() { return mysteryScrollGUI; }
    public ExtractorScroll getExtractorScroll() { return extractorScroll; }
    public ExtractorGUI getExtractorGUI() { return extractorGUI; }
    public EnchantForgeGUI getEnchantForgeGUI() { return enchantForgeGUI; }
    public AdminGUI getAdminGUI() { return adminGUI; }
    public TinkererGUI getTinkererGUI() { return tinkererGUI; }
    public AlchemistGUI getAlchemistGUI() { return alchemistGUI; }
    public DustItem getDustItem() { return dustItem; }
    public ProtectionScrolls getProtectionScrolls() { return protectionScrolls; }
    public VaultHook getVaultHook() { return vaultHook; }
    public SoulsManager getSoulsManager() { return soulsManager; }
    public ArmorSetManager getArmorSetManager() { return armorSetManager; }
    public WorldManager getWorldManager() { return worldManager; }
    public UpdateChecker getUpdateChecker() { return updateChecker; }
    public SlotIncreaser getSlotIncreaser() { return slotIncreaser; }
    public TransmogScroll getTransmogScroll() { return transmogScroll; }
    public RandomizationScroll getRandomizationScroll() { return randomizationScroll; }
    public HolyWhiteScroll getHolyWhiteScroll() { return holyWhiteScroll; }
    public EnchantShopGUI getEnchantShopGUI() { return enchantShopGUI; }
    public EnchantLimitManager getEnchantLimitManager() { return enchantLimitManager; }
    public EnchantComboManager getEnchantComboManager() { return enchantComboManager; }
    public EnchantEvolutionManager getEnchantEvolutionManager() { return enchantEvolutionManager; }
    public EnchantParticleManager getEnchantParticleManager() { return enchantParticleManager; }
}
