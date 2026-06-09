package com.vortexrpg.enchantments.enchant;

import com.vortexrpg.enchantments.VortexEnchantments;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.util.*;

/**
 * Central registry for all VortexEnchantments enchantments.
 * Handles registration, lookup, application/removal, and anvil combination logic.
 */
@SuppressWarnings("deprecation") // Legacy Spigot API (getLore/setLore/setDisplayName) intentional for broad compat
public class EnchantManager {

    private final VortexEnchantments plugin;
    private final Map<String, VortexEnchant> byId = new LinkedHashMap<>();
    private final Map<ItemTarget, List<VortexEnchant>> byTarget = new EnumMap<>(ItemTarget.class);
    // PDC key prefix stored on items: vortexenchantments:enchant_<id> -> level (int)

    public EnchantManager(VortexEnchantments plugin) {
        this.plugin = plugin;
    }

    // ─── Registration ────────────────────────────────────────────────────────

    public void registerAll() {
        byId.clear();
        byTarget.clear();

        // Sword
        register(new com.vortexrpg.enchantments.enchant.impl.sword.DebtEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.sword.HollowEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.sword.DormantEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.sword.ThirstEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.sword.MimicEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.sword.DroughtEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.sword.FractureEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.sword.EpitaphEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.sword.TetherEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.sword.KinesisEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.sword.TurncoatEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.sword.SeveranceEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.sword.OmenEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.sword.EntropyEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.sword.HarvestEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.sword.PhaseEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.sword.CatalystEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.sword.SiphonEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.sword.InversionEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.sword.EchoEnchant());

        // Sword — Reality & Dimension Manipulation
        register(new com.vortexrpg.enchantments.enchant.impl.sword.SingularityEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.sword.RiftWalkerEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.sword.DimensionSlashEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.sword.TesseractEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.sword.WormholeEnchant());

        // Sword — Time Manipulation
        register(new com.vortexrpg.enchantments.enchant.impl.sword.ChronostrikeEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.sword.TemporalEchoEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.sword.ParadoxBladeEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.sword.PhaseShiftEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.sword.CascadeRealmEnchant());

        // Sword — Entity Manipulation
        register(new com.vortexrpg.enchantments.enchant.impl.sword.PuppeteerEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.sword.DoppelgangerEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.sword.MarionetteEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.sword.SymbioteEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.sword.BansheeEnchant());

        // Sword — Physics-Bending Combat
        register(new com.vortexrpg.enchantments.enchant.impl.sword.GravityWellEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.sword.PrismEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.sword.AntimatterEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.sword.ShatterEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.sword.StormweaverEnchant());

        // Sword — Risk/Reward & Blood Magic
        register(new com.vortexrpg.enchantments.enchant.impl.sword.BloodPriceEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.sword.SoulbindEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.sword.KarmicDebtEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.sword.NecrosisEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.sword.OuroborosEnchant());

        // Sword — Psychological & Perception Warfare
        register(new com.vortexrpg.enchantments.enchant.impl.sword.DreamwalkerEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.sword.PlagueDoctorEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.sword.MirrorDimensionEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.sword.ProphecyEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.sword.ArchitectEnchant());

        // Sword — Ultimate Power
        register(new com.vortexrpg.enchantments.enchant.impl.sword.RagnarokEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.sword.CataclysmEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.sword.ApexEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.sword.VoidTouchEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.sword.PhantomBladeEnchant());

        // Sword — Soul & Chain Mechanics
        register(new com.vortexrpg.enchantments.enchant.impl.sword.SoulHarvestEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.sword.EchoChamberEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.sword.QuantumStrikeEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.sword.EntropyBladeEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.sword.LifelineEnchant());

        // Axe
        register(new com.vortexrpg.enchantments.enchant.impl.axe.VerdictEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.axe.CleaveEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.axe.RendEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.axe.GrudgeEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.axe.SchismEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.axe.OverloadEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.axe.TremorEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.axe.GougeEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.axe.BackswingEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.axe.RuinationEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.axe.PrimalEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.axe.FellEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.axe.SplinterEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.axe.WeightEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.axe.ReapEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.axe.FissureEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.axe.SeverEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.axe.TitanEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.axe.CullEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.axe.RicochetEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.axe.EarthquakeEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.axe.SunderEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.axe.BloodforgeEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.axe.UpheavalEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.axe.WarcryEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.axe.TectonicEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.axe.WhirlwindEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.axe.ConcussionEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.axe.GravediggerEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.axe.RampageEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.axe.JuggernautEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.axe.ColossusEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.axe.IroncladEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.axe.WildfireEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.axe.PermafrostEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.axe.ThorncallerEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.axe.StormfellEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.axe.RootgraspEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.axe.RiposteEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.axe.GladiatorEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.axe.MomentumEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.axe.DeflectEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.axe.CounterweightEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.axe.DreadnoughtEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.axe.BlightEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.axe.VenomCleaveEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.axe.HexSplitterEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.axe.DoomEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.axe.SiegeEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.axe.FortifyEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.axe.RallyEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.axe.DisarmEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.axe.ShieldbreakerEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.axe.MjolnirEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.axe.LeviathanEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.axe.WorldbreakerEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.axe.GodslayerEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.axe.ArmageddonEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.axe.TemporalAxeEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.axe.VampiricCleaveEnchant());

        // Bow
        register(new com.vortexrpg.enchantments.enchant.impl.bow.RefractionEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.bow.ThreadEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.bow.SplinterShotEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.bow.GravityEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.bow.HollowPointEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.bow.PinionEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.bow.WardenEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.bow.ThornArrowEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.bow.PendulumEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.bow.VeneerEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.bow.CircuitEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.bow.BallastEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.bow.SleeperEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.bow.ContrastEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.bow.LoanEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.bow.WhistleEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.bow.ProxyEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.bow.PinEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.bow.RiposteShotEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.bow.CascadeEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.bow.BarrageEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.bow.QuickdrawEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.bow.ShrapnelEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.bow.TracerEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.bow.FrostbiteEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.bow.RicochetEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.bow.VenomGlandEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.bow.RepeaterEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.bow.PhosphorEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.bow.TailwindEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.bow.PredatorEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.bow.DowndraftEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.bow.SniperEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.bow.ArterialEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.bow.GrappleEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.bow.MiasmaEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.bow.PhantomShotEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.bow.SureshotEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.bow.DecayEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.bow.PerforatorEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.bow.SiroccoEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.bow.CatalystEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.bow.DoppelgangerEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.bow.SoulArrowEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.bow.UpheavalEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.bow.StarchartEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.bow.NightfallEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.bow.EntropyEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.bow.ChimeraEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.bow.MindbreakerEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.bow.GenesisEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.bow.PandemoniumEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.bow.SupernovaEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.bow.SeraphEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.bow.PulsarEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.bow.RevenantEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.bow.ZodiacEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.bow.ApollyonEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.bow.TimestopEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.bow.WraithArrowEnchant());

        // Crossbow
        register(new com.vortexrpg.enchantments.enchant.impl.crossbow.SalvoEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.crossbow.DeadboltEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.crossbow.CrucibleEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.crossbow.RivetEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.crossbow.CaliberEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.crossbow.FeedbackEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.crossbow.ShatterEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.crossbow.BinaryEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.crossbow.FlakEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.crossbow.ReclaimEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.crossbow.OverchargeEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.crossbow.ThermalEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.crossbow.GyroEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.crossbow.BarricadeEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.crossbow.DeadeyeEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.crossbow.AmnesiaEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.crossbow.DischargeEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.crossbow.WakeEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.crossbow.PayloadEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.crossbow.FailsafeEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.crossbow.BuckshotEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.crossbow.ConcussionEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.crossbow.RecoilEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.crossbow.TracerRoundEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.crossbow.HamstringEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.crossbow.IronsightEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.crossbow.HeatseekerEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.crossbow.FragmentationEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.crossbow.GaleEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.crossbow.IncendiaryEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.crossbow.StrikerBoltEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.crossbow.HookshotEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.crossbow.TetherboltEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.crossbow.PenetratorEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.crossbow.RailgunEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.crossbow.NapalmEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.crossbow.OverclockEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.crossbow.VoltageEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.crossbow.MagnetboltEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.crossbow.SiphonboltEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.crossbow.TripwireEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.crossbow.BlackoutEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.crossbow.ResonantEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.crossbow.LockdownEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.crossbow.LeviathanEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.crossbow.CullingEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.crossbow.AnnihilatorEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.crossbow.DuskfireEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.crossbow.NullshotEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.crossbow.WidowmakerEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.crossbow.NemesisEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.crossbow.DominionEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.crossbow.ArmageddonEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.crossbow.PurgatoryEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.crossbow.ParadoxEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.crossbow.OrbitalEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.crossbow.ApocalypseEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.crossbow.ExtinctionEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.crossbow.WormholeEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.crossbow.AirburstEnchant());

        // Trident
        register(new com.vortexrpg.enchantments.enchant.impl.trident.UndertowEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.trident.DepthEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.trident.HarpoonEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.trident.FloodgateEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.trident.TorrentEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.trident.BarbEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.trident.ReefEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.trident.LeviathanEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.trident.MaelstromEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.trident.ShipbreakerEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.trident.BrineEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.trident.TempestEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.trident.AbyssalEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.trident.CoralEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.trident.NautilusEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.trident.DregEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.trident.EddyEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.trident.SurgeEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.trident.MarinerEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.trident.RecedeEnchant());

        // Spear
        register(new com.vortexrpg.enchantments.enchant.impl.spear.ImpalerEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.spear.PunctureEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.spear.VenomtipEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.spear.BarbedEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.spear.LongArmEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.spear.VaultingEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.spear.PinionEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.spear.PhalanxEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.spear.SidestepEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.spear.FlechetteEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.spear.GlacialEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.spear.TetherEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.spear.SpearwallEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.spear.SkewerEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.spear.RifttossEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.spear.RetrogradeEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.spear.LancetEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.spear.HarpoonEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.spear.HelixThrowEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.spear.PrismguardEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.spear.SandstormEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.spear.EchoStrikeEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.spear.SiphonEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.spear.TsunamiEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.spear.MirageEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.spear.JavelinEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.spear.ResonanceEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.spear.CascadeEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.spear.NetherspikeEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.spear.ReaverEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.spear.PetrifyEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.spear.ConstellationEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.spear.EidolonEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.spear.PhantasmalEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.spear.OrbitEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.spear.SingularityEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.spear.SoulthreadEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.spear.ZenithEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.spear.ChronospearEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.spear.OblivionEnchant());

        // Hammer
        register(new com.vortexrpg.enchantments.enchant.impl.hammer.ShatterEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hammer.PulverizeEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hammer.ShockwaveEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hammer.QuakeEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hammer.PummelEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hammer.DazeEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hammer.BulldozeEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hammer.IronwillEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hammer.ThermalEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hammer.StampedeEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hammer.BedrockEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hammer.BucklerEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hammer.RampartEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hammer.AnvilstrikeEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hammer.TremorsenseEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hammer.GroundpoundEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hammer.HammerfallEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hammer.SeismicwaveEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hammer.MagnetizeEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hammer.StonefistEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hammer.KineticEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hammer.MonolithEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hammer.SlagstrikeEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hammer.CollapsarEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hammer.MegatonEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hammer.MaelstromEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hammer.GigantifyEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hammer.BoneBreakerEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hammer.WardenEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hammer.ThunderClapEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hammer.UnstoppableEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hammer.RichterEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hammer.GravityWellEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hammer.DetonateEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hammer.NullfieldEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hammer.FaultlineEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hammer.CataclysmEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hammer.EclipseEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hammer.RagnarokEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hammer.VoidhammerEnchant());

        // Pickaxe
        register(new com.vortexrpg.enchantments.enchant.impl.pickaxe.AvariceEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.pickaxe.FossilEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.pickaxe.ResonanceEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.pickaxe.MemoryEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.pickaxe.CorewardEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.pickaxe.CompressEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.pickaxe.GeodeEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.pickaxe.MagnetismEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.pickaxe.DowsingEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.pickaxe.PressureEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.pickaxe.CoreTapEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.pickaxe.StratifyEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.pickaxe.ErosionEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.pickaxe.ArchiveEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.pickaxe.TributaryEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.pickaxe.HollowEarthEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.pickaxe.EchoLocateEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.pickaxe.TemperEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.pickaxe.CatalystPickEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.pickaxe.RecycleEnchant());
        // New Pickaxe Enchants
        register(new com.vortexrpg.enchantments.enchant.impl.pickaxe.SturdyPickEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.pickaxe.SpelunkerEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.pickaxe.RockSteadyEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.pickaxe.ChipperEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.pickaxe.ProspectorEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.pickaxe.VeinMinerEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.pickaxe.SmeltPickEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.pickaxe.ShockMineEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.pickaxe.StoneEaterEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.pickaxe.OreScoutEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.pickaxe.DeepDiveEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.pickaxe.QuarryEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.pickaxe.FlintStrikeEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.pickaxe.MotherlodeEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.pickaxe.SeismicPickEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.pickaxe.InfusionPickEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.pickaxe.TunnelBoreEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.pickaxe.CrystallizeEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.pickaxe.DeepVeinEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.pickaxe.PulverizeEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.pickaxe.NightVeinEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.pickaxe.GemCutterEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.pickaxe.CaveSenseEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.pickaxe.GoldRushEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.pickaxe.FissurePickEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.pickaxe.RefineryEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.pickaxe.TremorPickEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.pickaxe.MidasPickEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.pickaxe.LavaForgeEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.pickaxe.AftershockPickEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.pickaxe.OreBlossomEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.pickaxe.MassExcavatePickEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.pickaxe.DrillChargeEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.pickaxe.NexusTapEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.pickaxe.BountyStrikeEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.pickaxe.VoidBoreEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.pickaxe.WorldBreakerPickEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.pickaxe.AncientForgeEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.pickaxe.CoreSiphonEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.pickaxe.OmniMineEnchant());

        // Shovel
        register(new com.vortexrpg.enchantments.enchant.impl.shovel.ExcavateEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shovel.SiftEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shovel.QuicksandEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shovel.BurrowEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shovel.CompostEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shovel.LoamEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shovel.TerrainEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shovel.PeatEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shovel.UpstreamEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shovel.ArchaeologistEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shovel.LeveeEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shovel.TillerEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shovel.PermafrostEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shovel.LandslideEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shovel.DepositEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shovel.SinkholeEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shovel.MudslingerEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shovel.RootEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shovel.AlluvialEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shovel.TerraformEnchant());
        // New Shovel Enchants
        register(new com.vortexrpg.enchantments.enchant.impl.shovel.DirtShieldEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shovel.SoftDigEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shovel.PathMakerEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shovel.ClaySeekerEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shovel.GritEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shovel.TrenchEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shovel.SoilSamplerEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shovel.MulchEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shovel.DuneWalkerEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shovel.FrostDigEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shovel.WormDigEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shovel.GravelCrushEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shovel.HydroDigEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shovel.AvalancheEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shovel.TreasureDigEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shovel.EarthPulseEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shovel.CanyonDigEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shovel.SandblastEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shovel.FertilizeEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shovel.SubductionEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shovel.MudTrapEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shovel.GeologistEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shovel.DirtWallEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shovel.TectonicShovelEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shovel.MegaDigEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shovel.FossilDigEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shovel.EarthbendEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shovel.GroundPoundEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shovel.NutrientEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shovel.SandstormShovelEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shovel.LaylineEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shovel.EarthquakeShovelEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shovel.BuriedTreasureEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shovel.TerraSiphonEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shovel.DesertKingEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shovel.CataclysmShovelEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shovel.WorldShaperEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shovel.EarthcoreEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shovel.TerraNovaEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shovel.AbyssDigEnchant());

        // Hoe
        register(new com.vortexrpg.enchantments.enchant.impl.hoe.AbundanceEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hoe.SeasonEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hoe.CycleEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hoe.PollinateEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hoe.WeedkillerEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hoe.MulchEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hoe.HybridEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hoe.DroughtGuardEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hoe.ReaperEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hoe.ScarecrowEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hoe.NitrogenEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hoe.GerminateEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hoe.ThreshEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hoe.CanopyEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hoe.PlowEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hoe.VerdantEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hoe.HarvestMoonEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hoe.WormEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hoe.OvergrowthEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hoe.WinnowEnchant());
        // New hoe enchants
        register(new com.vortexrpg.enchantments.enchant.impl.hoe.GreenThumbEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hoe.RootCutterEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hoe.CompostHoeEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hoe.SoilTurnerEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hoe.SeedSaverEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hoe.CropRotationEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hoe.SproutEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hoe.FertileGroundEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hoe.HayMakerEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hoe.SunriseEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hoe.IrrigateEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hoe.GraftEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hoe.CultivatorEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hoe.BountifulEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hoe.PruneEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hoe.FungalSpreadEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hoe.SilkSproutEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hoe.NaturesPactEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hoe.TrellisEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hoe.GoldenHarvestEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hoe.DecomposerEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hoe.MycorrhizaEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hoe.AutoSowEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hoe.PlagueSpreaderEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hoe.FloraShieldEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hoe.LifeLeechHoeEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hoe.FieldClearEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hoe.PhotosynthesisEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hoe.TerraceEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hoe.EvergreenEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hoe.BrambleTrapEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hoe.GaiasTouchEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hoe.HarvestKingEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hoe.WorldTreeEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hoe.FaminesBaneEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hoe.EdenEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hoe.PrimordialBloomEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hoe.NaturesWrathEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hoe.LifeWeaverEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.hoe.EternalHarvestEnchant());

        // Helmet
        register(new com.vortexrpg.enchantments.enchant.impl.helmet.ParadoxEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.helmet.ForesightEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.helmet.RecallEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.helmet.ClarityEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.helmet.BabelEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.helmet.SonarEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.helmet.ThirdEyeEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.helmet.CognitionEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.helmet.CrownEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.helmet.LucidEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.helmet.MaskEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.helmet.ArchiveHelmetEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.helmet.MigraineEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.helmet.OracleEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.helmet.SentinelEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.helmet.DiademEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.helmet.PressureSealEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.helmet.CipherEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.helmet.VertigoEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.helmet.CacheEnchant());
        // New Helmet Enchantments
        register(new com.vortexrpg.enchantments.enchant.impl.helmet.PaddedEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.helmet.VentilateEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.helmet.BraceEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.helmet.CraniumEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.helmet.AlertnessEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.helmet.PremonitionEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.helmet.DowseEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.helmet.AetherEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.helmet.ReflexEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.helmet.EchoEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.helmet.NightbloomEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.helmet.MindshieldEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.helmet.HaloEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.helmet.BrainstormEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.helmet.CatalystHelmEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.helmet.PsycheEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.helmet.OverwatchEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.helmet.SpecterEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.helmet.NeurolinkEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.helmet.FocalEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.helmet.PrismEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.helmet.WardingEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.helmet.PerceiveEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.helmet.OmniscientEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.helmet.DreamweaverEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.helmet.HypnosisEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.helmet.GravemindEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.helmet.SynapticEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.helmet.AstralEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.helmet.ApexEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.helmet.MirageEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.helmet.AllseerEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.helmet.TranscendenceEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.helmet.MindforgeEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.helmet.AegisCrownEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.helmet.ZenithEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.helmet.OmnipotentSightEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.helmet.CelestialCrownEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.helmet.VoidEyeEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.helmet.WorldSenseEnchant());

        // Chestplate
        register(new com.vortexrpg.enchantments.enchant.impl.chestplate.BacklashEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.chestplate.EquilibriumEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.chestplate.SymbiosisEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.chestplate.AdrenalineEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.chestplate.PhotosynthesisEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.chestplate.FortifyEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.chestplate.ExoskeletonEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.chestplate.VortexArmorEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.chestplate.ConvertEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.chestplate.AegisEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.chestplate.ScarTissueEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.chestplate.MantleEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.chestplate.ReservoirEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.chestplate.PhantomPlateEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.chestplate.ConductionEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.chestplate.ReciprocityEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.chestplate.IronheartEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.chestplate.DeflectEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.chestplate.CocoonEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.chestplate.EmbargoEnchant());
        // New Chestplate Enchantments
        register(new com.vortexrpg.enchantments.enchant.impl.chestplate.PaddedVestEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.chestplate.LayeredEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.chestplate.BreathplateEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.chestplate.CushionEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.chestplate.InsulateEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.chestplate.ThornsplateEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.chestplate.ReplenishEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.chestplate.SteadfastEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.chestplate.GritEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.chestplate.ConduitPlateEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.chestplate.ReactiveEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.chestplate.VitalityEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.chestplate.LinkguardEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.chestplate.AbsorbPlateEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.chestplate.GuardianPlateEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.chestplate.CounterstrikeEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.chestplate.RadianceEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.chestplate.StalwartEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.chestplate.EntangleEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.chestplate.RuneShieldEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.chestplate.OverchargeEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.chestplate.FlameguardEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.chestplate.BerserkerPlateEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.chestplate.VenomPlatingEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.chestplate.TemporalPlateEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.chestplate.ReverberateEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.chestplate.CorrosionEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.chestplate.MartyrdomEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.chestplate.PhaseShiftEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.chestplate.SoulboundPlateEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.chestplate.IndomitableEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.chestplate.RegeneratePlateEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.chestplate.NovaplateEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.chestplate.UnbreakableWillEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.chestplate.EternalGuardEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.chestplate.GodlikeEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.chestplate.InfinityPlateEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.chestplate.ObliterateEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.chestplate.ApotheosisEnchant());

        // Leggings
        register(new com.vortexrpg.enchantments.enchant.impl.leggings.TitheEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.leggings.StrideEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.leggings.RootLeggingsEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.leggings.NimbleEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.leggings.LadenEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.leggings.BriarEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.leggings.ThermalLeggingsEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.leggings.FluxEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.leggings.GroundedEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.leggings.PhantomLimbEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.leggings.ObeliskEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.leggings.MomentumEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.leggings.TangleEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.leggings.CounterEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.leggings.FurnaceEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.leggings.GaitEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.leggings.IronlegEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.leggings.PenitentEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.leggings.TraverseEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.leggings.PulseEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.leggings.CushionedGreavesEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.leggings.FireproofLegsEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.leggings.LegBraceEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.leggings.ArmoredLegsEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.leggings.SatedEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.leggings.EntrapEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.leggings.ShockAbsorbEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.leggings.SwiftGreavesEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.leggings.ResilienceEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.leggings.FortifiedLegsEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.leggings.StoneskinEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.leggings.AggressorEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.leggings.MetabolismEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.leggings.ShockwaveEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.leggings.AdrenalineLegsEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.leggings.LeechEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.leggings.StanceBreakerEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.leggings.BulwarkGreavesEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.leggings.MirrorLegsEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.leggings.VendettaEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.leggings.IronWillEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.leggings.RegenLegsEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.leggings.AbsorbLegsEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.leggings.RetaliationFieldEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.leggings.LastStandEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.leggings.IntimidateEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.leggings.PhaseGreavesEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.leggings.TitanGreavesEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.leggings.OverpowerLegsEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.leggings.ThornsAuraEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.leggings.WardenLegsEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.leggings.GravityWellEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.leggings.UnyieldingLegsEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.leggings.AdaptiveArmorEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.leggings.CommanderLegsEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.leggings.ExecutionerLegsEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.leggings.ColossusEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.leggings.OblivionEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.leggings.WarGodEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.leggings.ApotheosisEnchant());

        // Boots
        register(new com.vortexrpg.enchantments.enchant.impl.boots.AnchorEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.boots.TransposeEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.boots.FoxfootEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.boots.PathfinderEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.boots.ThermalStepEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.boots.TrailEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.boots.CraterEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.boots.MoonwalkEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.boots.TremorsenseEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.boots.PropulsionEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.boots.LandmineEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.boots.StaticEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.boots.BogWalkerEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.boots.ReverbEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.boots.DriftEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.boots.LodestoneEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.boots.AfterburnEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.boots.GroundswellEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.boots.NomadEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.boots.VaultEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.boots.PaddedSolesEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.boots.LightStepEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.boots.FireWalkerEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.boots.IronSolesEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.boots.NourishEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.boots.IronAnkleEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.boots.DustTrailEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.boots.CushionBootEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.boots.QuickDashEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.boots.NightRunnerEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.boots.RecoilEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.boots.GroundedHealEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.boots.ShockSoleEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.boots.ImpactCraterEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.boots.FrostKickEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.boots.WindStrideEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.boots.LeapBootEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.boots.KiteTacticsEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.boots.VigorStrideEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.boots.StompingGroundEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.boots.FeatherfallPlusEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.boots.PursuitBootsEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.boots.EntangleEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.boots.EarthquakeBootsEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.boots.PhantomStepEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.boots.AdrenalineRushEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.boots.TitanAnkleEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.boots.BlitzEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.boots.LifeStealBootsEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.boots.SpikedSolesEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.boots.FortifiedAnkleEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.boots.WrathOfGroundEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.boots.PhoenixSoleEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.boots.CommanderBootsEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.boots.ExecutionerBootsEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.boots.WarCryEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.boots.BlinkEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.boots.GodstepEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.boots.RampageEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.boots.VoidStepEnchant());

        // Shield
        register(new com.vortexrpg.enchantments.enchant.impl.shield.TributeEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shield.RefractionShieldEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shield.AegisPulseEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shield.AbsorbEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shield.BeaconEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shield.TauntEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shield.RebuffEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shield.ExorciseEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shield.RampartEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shield.DeflectArcEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shield.FortressEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shield.CounterspellEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shield.BucklerChargeEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shield.MartyrEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shield.PhalanxEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shield.RiposteShieldEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shield.IronbarkEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shield.MirrorEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shield.WardEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shield.OverwatchEnchant());
        // New shield enchants
        register(new com.vortexrpg.enchantments.enchant.impl.shield.BraceEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shield.ShieldBashEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shield.SteadyGuardEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shield.PaddedShieldEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shield.AlertEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shield.ThornGuardEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shield.RecoveryEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shield.BulwarkEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shield.ArrowCatchEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shield.ShockAbsorbEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shield.ProwlEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shield.VanguardEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shield.StaggerEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shield.ElementalGuardEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shield.ShieldWallEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shield.MagneticShieldEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shield.EchoBlockEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shield.ArcaneBarrierEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shield.BloodShieldEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shield.HeavyBashEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shield.PetrifyEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shield.SpinBlockEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shield.SentinelEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shield.VoidShellEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shield.ResonanceShieldEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shield.WarCryEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shield.ShieldChargeEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shield.LifeBarrierEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shield.PhantomGuardEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shield.RetributionEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shield.TempestShieldEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shield.UnyieldingEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shield.DivineAegisEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shield.ParagonEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shield.ArchonShieldEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shield.LastStandShieldEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shield.WorldGuardianEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shield.InfiniteAegisEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shield.AnnihilationWallEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.shield.OmniShieldEnchant());

        // Elytra
        register(new com.vortexrpg.enchantments.enchant.impl.elytra.JetstreamEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.elytra.ThermalRiderEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.elytra.StormchaserEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.elytra.SonicEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.elytra.FeatherweightEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.elytra.DiveBombEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.elytra.CloudwalkEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.elytra.TailwindEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.elytra.DecoyEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.elytra.AerobatEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.elytra.SentryEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.elytra.ParachuteEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.elytra.ContrailEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.elytra.UpdraftEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.elytra.InterceptorEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.elytra.MantleShiftEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.elytra.CapacitorElytraEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.elytra.HarrierEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.elytra.NovaEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.elytra.WingspanEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.elytra.GliderEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.elytra.AirCushionEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.elytra.SteadyFlightEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.elytra.BreezeBornEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.elytra.SoftLandingEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.elytra.SlipstreamEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.elytra.AirBrakeEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.elytra.EagleEyeEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.elytra.PropulsionEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.elytra.BirdOfPreyEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.elytra.AltitudeEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.elytra.AeroLiftEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.elytra.GustRiderEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.elytra.SkyforgeEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.elytra.AirSiphonEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.elytra.GaleStrikeEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.elytra.AfterburnerEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.elytra.NimbusEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.elytra.DraftDancerEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.elytra.AeroShieldEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.elytra.BarrelRollEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.elytra.CloudBurstEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.elytra.ThermalSurgeEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.elytra.SkylordEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.elytra.FlightFuryEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.elytra.StratosphereEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.elytra.PhoenixWingEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.elytra.GravityWellEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.elytra.TurbulenceEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.elytra.StormWingEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.elytra.AetherDashEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.elytra.CelestialEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.elytra.WarpglideEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.elytra.AetherPulseEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.elytra.SkyquakeEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.elytra.ZephyrEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.elytra.AscendantEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.elytra.EternalFlightEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.elytra.VoidWalkerEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.elytra.SkySovereignEnchant());

        // Fishing Rod
        register(new com.vortexrpg.enchantments.enchant.impl.fishingrod.BarterEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.fishingrod.SonarRodEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.fishingrod.PatienceEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.fishingrod.DepthFinderEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.fishingrod.ChumEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.fishingrod.TrawlEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.fishingrod.MoonfishEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.fishingrod.WranglerEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.fishingrod.UndertowRodEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.fishingrod.BycatchEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.fishingrod.SalvageEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.fishingrod.LureCraftEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.fishingrod.ReboundEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.fishingrod.LeviathanHookEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.fishingrod.WhirlpoolEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.fishingrod.BiomeBonusEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.fishingrod.StormFisherEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.fishingrod.AnchorLineEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.fishingrod.DragnetEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.fishingrod.TaxidermyEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.fishingrod.QuickCastEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.fishingrod.SturdyLineEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.fishingrod.DoubleHookEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.fishingrod.FreshCatchEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.fishingrod.FloatLineEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.fishingrod.RiptideRodEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.fishingrod.NetCasterEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.fishingrod.TidalLureEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.fishingrod.VenomHookEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.fishingrod.ReelMasterEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.fishingrod.BaitLoopEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.fishingrod.CoralFinderEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.fishingrod.AnglersLuckEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.fishingrod.GrappleEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.fishingrod.TidePullEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.fishingrod.TreasureHunterEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.fishingrod.ElectricLineEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.fishingrod.PiranhaEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.fishingrod.SeafoamEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.fishingrod.DualCatchEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.fishingrod.SirenSongEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.fishingrod.AquaPulseEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.fishingrod.HookShotEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.fishingrod.MarinersWrathEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.fishingrod.FathomReelEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.fishingrod.TsunamiCastEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.fishingrod.PhantomHookEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.fishingrod.VortexReelEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.fishingrod.PoseidonsGiftEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.fishingrod.AbyssalLineEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.fishingrod.LeviathansGraspEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.fishingrod.KrakenCallEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.fishingrod.DeepSeaKingEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.fishingrod.MaelstromEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.fishingrod.MidasReelEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.fishingrod.AbyssalReelEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.fishingrod.OceanSovereignEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.fishingrod.WorldFisherEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.fishingrod.TidalDominanceEnchant());
        register(new com.vortexrpg.enchantments.enchant.impl.fishingrod.EternalAnglerEnchant());

        plugin.getLogger().info("Registered " + byId.size() + " VortexEnchantments.");
        loadAllConfigs();
    }

    private void register(VortexEnchant enchant) {
        byId.put(enchant.getId(), enchant);
        for (ItemTarget target : enchant.getTargets()) {
            byTarget.computeIfAbsent(target, k -> new ArrayList<>()).add(enchant);
        }
    }

    private void loadAllConfigs() {
        for (VortexEnchant enchant : byId.values()) {
            loadConfig(enchant);
        }
    }

    public void loadConfig(VortexEnchant enchant) {
        String path = "enchants/" + getCategory(enchant) + "/" + enchant.getId() + ".yml";
        File configFile = new File(plugin.getDataFolder(), path);
        if (!configFile.exists()) {
            plugin.saveResource(path, false);
        }
        YamlConfiguration yamlConfig = YamlConfiguration.loadConfiguration(configFile);
        enchant.setEnabled(yamlConfig.getBoolean("enabled", true));
        List<String> conflicts = yamlConfig.getStringList("conflicts");
        enchant.setConflicts(conflicts);
        enchant.setConfig(yamlConfig.getConfigurationSection("settings") != null
            ? yamlConfig.getConfigurationSection("settings")
            : yamlConfig);
    }

    private String getCategory(VortexEnchant enchant) {
        if (enchant.getTargets().isEmpty()) return "misc";
        String name = enchant.getTargets().get(0).name().toLowerCase();
        return name.replace("_", "");
    }

    // ─── Lookup ──────────────────────────────────────────────────────────────

    public VortexEnchant getById(String id) {
        return byId.get(id.toLowerCase());
    }

    public List<VortexEnchant> getAll() {
        return new ArrayList<>(byId.values());
    }

    public List<VortexEnchant> getForTarget(ItemTarget target) {
        return byTarget.getOrDefault(target, Collections.emptyList());
    }

    public int getEnchantCount() {
        return byId.size();
    }

    // ─── Item application ────────────────────────────────────────────────────

    /**
     * Apply an enchant at the given level to an item via PDC.
     * Also updates the item's lore to show the enchantment.
     */
    public void applyEnchant(ItemStack item, VortexEnchant enchant, int level) {
        if (item == null || item.getType() == Material.AIR) return;
        level = Math.max(1, Math.min(level, enchant.getMaxLevel()));

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        NamespacedKey key = enchant.getPDCKey();
        meta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, level);

        // Update lore
        List<String> lore = meta.getLore() != null ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
        // Remove old entry for this enchant if present
        String prefix = enchant.getRarity().getColor() + enchant.getDisplayName();
        lore.removeIf(line -> line.startsWith(prefix));
        lore.add(0, enchant.getLoreLine(level));
        meta.setLore(lore);

        // Add enchantment glint (like vanilla enchanted items)
        meta.setEnchantmentGlintOverride(true);

        item.setItemMeta(meta);
    }

    /**
     * Remove an enchant from an item.
     */
    public void removeEnchant(ItemStack item, VortexEnchant enchant) {
        if (item == null || item.getType() == Material.AIR) return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        meta.getPersistentDataContainer().remove(enchant.getPDCKey());

        // Remove lore entry
        if (meta.getLore() != null) {
            String prefix = enchant.getRarity().getColor() + enchant.getDisplayName();
            List<String> lore = new ArrayList<>(meta.getLore());
            lore.removeIf(line -> line.startsWith(prefix));
            meta.setLore(lore);
        }

        // Remove enchant glint if no VortexEnchants remain on the item
        boolean hasAny = false;
        for (VortexEnchant e : byId.values()) {
            if (meta.getPersistentDataContainer().has(e.getPDCKey(), PersistentDataType.INTEGER)) {
                hasAny = true;
                break;
            }
        }
        if (!hasAny) {
            meta.setEnchantmentGlintOverride(null); // reset to default
        }

        item.setItemMeta(meta);
    }

    /**
     * Get the level of an enchant on an item (0 = not present).
     */
    public int getLevel(ItemStack item, VortexEnchant enchant) {
        if (item == null || item.getType() == Material.AIR) return 0;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return 0;
        Integer level = meta.getPersistentDataContainer().get(enchant.getPDCKey(), PersistentDataType.INTEGER);
        return level != null ? level : 0;
    }

    public int getLevel(ItemStack item, String enchantId) {
        VortexEnchant enchant = getById(enchantId);
        return enchant != null ? getLevel(item, enchant) : 0;
    }

    /**
     * Returns map of all VortexEnchants on an item with their levels.
     */
    public Map<VortexEnchant, Integer> getEnchants(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return Collections.emptyMap();
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return Collections.emptyMap();

        Map<VortexEnchant, Integer> result = new LinkedHashMap<>();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();

        for (VortexEnchant enchant : byId.values()) {
            Integer level = pdc.get(enchant.getPDCKey(), PersistentDataType.INTEGER);
            if (level != null && level > 0) {
                result.put(enchant, level);
            }
        }
        return result;
    }

    /**
     * Check if two enchants conflict.
     */
    public boolean conflicts(VortexEnchant a, VortexEnchant b) {
        return a.getConflicts().contains(b.getId()) || b.getConflicts().contains(a.getId());
    }

    /**
     * Check if applying this enchant to the item would create a conflict.
     */
    public boolean wouldConflict(ItemStack item, VortexEnchant enchant) {
        for (VortexEnchant existing : getEnchants(item).keySet()) {
            if (conflicts(existing, enchant)) return true;
        }
        return false;
    }

    /**
     * Creates an enchanted book with the given enchant and level.
     */
    public ItemStack createEnchantedBook(VortexEnchant enchant, int level) {
        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = book.getItemMeta();
        if (meta == null) return book;

        meta.getPersistentDataContainer().set(enchant.getPDCKey(), PersistentDataType.INTEGER, level);
        meta.setDisplayName(enchant.getLoreLine(level));
        List<String> lore = new ArrayList<>();
        lore.add(enchant.rarity.getColor() + "§l" + enchant.rarity.getDisplayName());
        lore.add("§7" + enchant.getDescription(level));
        lore.add("§8Level: " + level + "/" + enchant.getMaxLevel());
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
        book.setItemMeta(meta);
        return book;
    }

    /**
     * Combine two items (anvil logic): transfer enchants from sacrifice to target.
     */
    public ItemStack combineItems(ItemStack target, ItemStack sacrifice) {
        if (target == null || sacrifice == null) return target;

        // Get enchants from sacrifice (could be enchanted book or tool)
        Map<VortexEnchant, Integer> sacrificeEnchants = getEnchants(sacrifice);
        if (sacrificeEnchants.isEmpty()) return target;

        ItemStack result = target.clone();
        for (Map.Entry<VortexEnchant, Integer> entry : sacrificeEnchants.entrySet()) {
            VortexEnchant enchant = entry.getKey();
            int sacrificeLevel = entry.getValue();

            if (!isCompatible(result, enchant)) continue;
            if (wouldConflict(result, enchant)) continue;

            int currentLevel = getLevel(result, enchant);
            int newLevel;
            if (currentLevel == sacrificeLevel) {
                newLevel = Math.min(currentLevel + 1, enchant.getMaxLevel());
            } else {
                newLevel = Math.max(currentLevel, sacrificeLevel);
            }
            applyEnchant(result, enchant, newLevel);
        }
        return result;
    }

    private boolean isCompatible(ItemStack item, VortexEnchant enchant) {
        for (ItemTarget target : enchant.getTargets()) {
            if (target.matches(item.getType())) return true;
        }
        // Books can hold any enchant
        return item.getType() == Material.ENCHANTED_BOOK || item.getType() == Material.BOOK;
    }

    public void reloadAll() {
        loadAllConfigs();
    }
}
