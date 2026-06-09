package com.vortexrpg.enchantments.fabric;

import com.vortexrpg.enchantments.fabric.command.VeCommand;
import com.vortexrpg.enchantments.fabric.core.EnchantRegistry;
import com.vortexrpg.enchantments.fabric.core.Enchants;
import com.vortexrpg.enchantments.fabric.event.EventDispatch;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Native Fabric edition of VortexEnchantments.
 *
 * <p>This is a from-scratch port of the Paper plugin: enchant data is stored in the item's
 * {@code minecraft:custom_data} component, gameplay is driven by Fabric API events plus a small
 * combat mixin, and there is no dependency on Cardboard or the Bukkit API.
 */
public class VortexEnchantmentsMod implements ModInitializer {

    public static final String MOD_ID = "vortexenchantments";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("[VortexEnchantments] Fabric edition initializing...");
        Enchants.registerAll();
        EventDispatch.register();
        CommandRegistrationCallback.EVENT.register((dispatcher, access, environment) ->
                VeCommand.register(dispatcher));
        LOGGER.info("[VortexEnchantments] Ready: {} enchant(s) registered.", EnchantRegistry.count());
    }
}
