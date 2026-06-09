package com.vortexrpg.enchantments.enchant.impl.axe;

import com.vortexrpg.enchantments.enchant.EnchantRarity;
import com.vortexrpg.enchantments.enchant.ItemTarget;
import com.vortexrpg.enchantments.enchant.VortexEnchant;
import com.vortexrpg.enchantments.util.ParticleUtil;
import com.vortexrpg.enchantments.util.SoundUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * HexSplitter: Attacks strip all beneficial potion effects from the target.
 * Level 1: Removes 1 random buff. Level 2: Removes 2. Level 3: Removes ALL.
 */
public class HexSplitterEnchant extends VortexEnchant {

    public HexSplitterEnchant() {
        super("hexsplitter", "Hex Splitter", EnchantRarity.LEGENDARY, 3, List.of(ItemTarget.AXE));
    }

    @Override
    public void onAttack(EntityDamageByEntityEvent event, Player attacker, LivingEntity victim, int level) {
        if (!isEnabled()) return;

        double chance = cfgd("chance", 0.3 + level * 0.1);
        if (Math.random() > chance) return;

        int maxRemove = cfgi("max_remove", level >= 3 ? 99 : level);

        List<PotionEffect> buffs = victim.getActivePotionEffects().stream()
                .filter(e -> isBeneficial(e.getType()))
                .toList();

        if (buffs.isEmpty()) return;

        int removed = 0;
        for (PotionEffect buff : buffs) {
            if (removed >= maxRemove) break;
            victim.removePotionEffect(buff.getType());
            removed++;
        }

        ParticleUtil.spawn(victim.getLocation().add(0, 1.5, 0), Particle.WITCH, 15, 0.5);
        SoundUtil.play(victim.getLocation(), Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, 0.8f, 0.5f);

        if (victim instanceof Player p) {
            p.sendMessage("§5[Hex Splitter] §7" + removed + " buff(s) stripped!");
        }
    }

    private boolean isBeneficial(PotionEffectType type) {
        return type.equals(PotionEffectType.SPEED) ||
                type.equals(PotionEffectType.STRENGTH) ||
                type.equals(PotionEffectType.REGENERATION) ||
                type.equals(PotionEffectType.RESISTANCE) ||
                type.equals(PotionEffectType.FIRE_RESISTANCE) ||
                type.equals(PotionEffectType.ABSORPTION) ||
                type.equals(PotionEffectType.HASTE) ||
                type.equals(PotionEffectType.INVISIBILITY) ||
                type.equals(PotionEffectType.JUMP_BOOST);
    }

    @Override
    public String getDescription(int level) {
        String amount = level >= 3 ? "ALL" : String.valueOf(level);
        return "§7Attacks strip §5" + amount + " §7beneficial effect(s) from target.";
    }
}
