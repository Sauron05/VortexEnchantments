package com.vortexrpg.enchantments.enchant.impl.spear;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.List;

/**
 * Soulthread: Links your soul with the target for 4/6/8 seconds.
 * During that time, the target takes 25/35/45% of ALL damage you receive.
 * A devastating revenge mechanic.
 */
public class SoulthreadEnchant extends VortexEnchant {

    private static final Map<UUID, UUID> LINKED = new HashMap<>();

    public SoulthreadEnchant() {
        super("soulthread", "Soulthread", EnchantRarity.MYTHIC, 3, List.of(ItemTarget.SPEAR));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;
        if (isOnCooldown(attacker)) return;
        if (LINKED.containsKey(attacker.getUniqueId())) return;

        int durationTicks = cfgi("duration_ticks", (2 + level * 2) * 20);
        cfgd("transfer_percent", 0.15 + level * 0.10);

        LINKED.put(attacker.getUniqueId(), victim.getUniqueId());

        ParticleUtil.drawLine(attacker.getLocation().add(0, 1, 0),
                victim.getLocation().add(0, 1, 0), Particle.SOUL, 0.3);
        SoundUtil.play(attacker.getLocation(), Sound.ENTITY_WARDEN_HEARTBEAT, 0.8f, 1.5f);

        attacker.sendMessage("§d[Soulthread] §7Your souls are linked!");
        if (victim instanceof Player p) {
            p.sendMessage("§d[Soulthread] §7Your soul has been bound!");
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                LINKED.remove(attacker.getUniqueId());
            }
        }.runTaskLater(JavaPlugin.getProvidingPlugin(getClass()), durationTicks);

        setCooldownFromConfig(attacker, "cooldown", 25);
    }

    @Override
    public void onDamaged(EntityDamageByEntityEvent event, Player victim, Entity attacker, int level) {
        if (!isEnabled()) return;

        UUID linkedId = LINKED.get(victim.getUniqueId());
        if (linkedId == null) return;

        double transferPct = cfgd("transfer_percent", 0.15 + level * 0.10);

        org.bukkit.entity.Entity linkedEntity = org.bukkit.Bukkit.getEntity(linkedId);
        if (linkedEntity instanceof LivingEntity le && le.isValid() && !le.isDead()) {
            double transferDmg = event.getDamage() * transferPct;
            le.damage(transferDmg);

            ParticleUtil.drawLine(victim.getLocation().add(0, 1, 0),
                    le.getLocation().add(0, 1, 0), Particle.SOUL, 0.4);
        }
    }

    @Override
    public String getDescription(int level) {
        int pct = (int) ((0.15 + level * 0.10) * 100);
        int secs = 2 + level * 2;
        return "§7Link souls " + secs + "s: target takes §d" + pct + "% §7of YOUR received damage.";
    }
}
