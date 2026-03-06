package dev.raikou.raikouhud.command;

import dev.raikou.raikouhud.command.permission.PermissionNode;
import dev.raikou.raikouhud.command.subcommand.Subcommand;
import dev.raikou.raikouhud.i18n.MessageKey;
import dev.raikou.raikouhud.i18n.MessageService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public final class RaikouHudCommand implements CommandExecutor, TabCompleter {

    private final Logger logger;
    private final MessageService messageService;
    private final Map<String, Subcommand> subcommands;

    public RaikouHudCommand(Logger logger, MessageService messageService, Collection<Subcommand> subcommands) {
        this.logger = Objects.requireNonNull(logger, "logger");
        this.messageService = Objects.requireNonNull(messageService, "messageService");
        this.subcommands = new HashMap<>();
        for (Subcommand subcommand : subcommands) {
            this.subcommands.put(subcommand.name().toLowerCase(Locale.ROOT), subcommand);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!hasPermission(sender, PermissionNode.USE.node())) {
            messageService.send(sender, MessageKey.COMMAND_NO_PERMISSION);
            return true;
        }

        String subcommandName = args.length == 0 ? "help" : args[0].toLowerCase(Locale.ROOT);
        Subcommand subcommand = subcommands.get(subcommandName);
        if (subcommand == null) {
            messageService.send(sender, MessageKey.COMMAND_UNKNOWN_SUBCOMMAND);
            return true;
        }

        if (!hasPermission(sender, subcommand.permission())) {
            messageService.send(sender, MessageKey.COMMAND_NO_PERMISSION);
            return true;
        }

        if (subcommand.playerOnly() && !(sender instanceof Player)) {
            messageService.send(sender, MessageKey.COMMAND_PLAYER_ONLY);
            return true;
        }

        try {
            String[] shifted = args.length <= 1 ? new String[0] : Arrays.copyOfRange(args, 1, args.length);
            subcommand.execute(sender, shifted);
        } catch (Exception exception) {
            logger.warning("Command execution failed for subcommand '" + subcommand.name() + "': " + exception.getMessage());
            messageService.send(sender, MessageKey.COMMAND_RELOAD_FAILED);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String partial = args[0].toLowerCase(Locale.ROOT);
            List<String> values = new ArrayList<>();
            for (Subcommand subcommand : subcommands.values()) {
                if (!hasPermission(sender, subcommand.permission())) {
                    continue;
                }
                String name = subcommand.name().toLowerCase(Locale.ROOT);
                if (name.startsWith(partial)) {
                    values.add(name);
                }
            }
            return values;
        }

        String subcommandName = args[0].toLowerCase(Locale.ROOT);
        Subcommand subcommand = subcommands.get(subcommandName);
        if (subcommand == null || !hasPermission(sender, subcommand.permission())) {
            return List.of();
        }
        String[] shifted = Arrays.copyOfRange(args, 1, args.length);
        return subcommand.tabComplete(sender, shifted);
    }

    private boolean hasPermission(CommandSender sender, String permission) {
        return sender.hasPermission(permission) || sender.hasPermission(PermissionNode.ADMIN.node());
    }
}

