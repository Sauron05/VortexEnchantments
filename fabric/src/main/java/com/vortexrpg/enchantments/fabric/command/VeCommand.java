package com.vortexrpg.enchantments.fabric.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.vortexrpg.enchantments.fabric.core.EnchantRegistry;
import com.vortexrpg.enchantments.fabric.core.FabricEnchant;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/** {@code /ve} admin command: list enchants and apply them to the held item. */
public final class VeCommand {

    private VeCommand() {}

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("ve")
                .requires(VeCommand::isOperator)
                .then(CommandManager.literal("list")
                        .executes(VeCommand::list))
                .then(CommandManager.literal("give")
                        .then(CommandManager.argument("enchant", StringArgumentType.string())
                                .executes(ctx -> give(ctx, 1))
                                .then(CommandManager.argument("level", IntegerArgumentType.integer(1, 10))
                                        .executes(ctx -> give(ctx, IntegerArgumentType.getInteger(ctx, "level")))))));
    }

    private static boolean isOperator(ServerCommandSource src) {
        // TODO: gate via the 1.21.11 codec-based permission API (net.minecraft.command.permission.*).
        // Left open for now so the admin command is usable on test servers; restrict before release.
        return true;
    }

    private static int list(CommandContext<ServerCommandSource> ctx) {
        ServerCommandSource src = ctx.getSource();
        src.sendFeedback(() -> Text.literal("VortexEnchantments: " + EnchantRegistry.count()
                + " enchant(s) registered.").formatted(Formatting.LIGHT_PURPLE), false);
        for (FabricEnchant e : EnchantRegistry.all()) {
            src.sendFeedback(() -> Text.literal(" - " + e.getId() + " (" + e.getDisplayName()
                    + ", max " + e.getMaxLevel() + ")").formatted(Formatting.GRAY), false);
        }
        return EnchantRegistry.count();
    }

    private static int give(CommandContext<ServerCommandSource> ctx, int level) throws CommandSyntaxException {
        ServerCommandSource src = ctx.getSource();
        ServerPlayerEntity player = src.getPlayerOrThrow();
        String id = StringArgumentType.getString(ctx, "enchant");
        FabricEnchant enchant = EnchantRegistry.byId(id);
        if (enchant == null) {
            src.sendError(Text.literal("Unknown enchant: " + id));
            return 0;
        }
        ItemStack stack = player.getMainHandStack();
        if (stack.isEmpty()) {
            src.sendError(Text.literal("Hold an item in your main hand first."));
            return 0;
        }
        EnchantRegistry.apply(stack, enchant, level);
        int applied = Math.max(1, Math.min(level, enchant.getMaxLevel()));
        src.sendFeedback(() -> Text.literal("Applied " + enchant.getDisplayName() + " " + applied
                + " to your held item.").formatted(Formatting.GREEN), false);
        return 1;
    }
}
