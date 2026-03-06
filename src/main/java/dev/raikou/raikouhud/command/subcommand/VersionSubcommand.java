package dev.raikou.raikouhud.command.subcommand;

import dev.raikou.raikouhud.i18n.MessageKey;
import dev.raikou.raikouhud.i18n.MessageService;
import java.util.Map;
import java.util.Objects;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public final class VersionSubcommand implements Subcommand {

    private final JavaPlugin plugin;
    private final MessageService messageService;

    public VersionSubcommand(JavaPlugin plugin, MessageService messageService) {
        this.plugin = Objects.requireNonNull(plugin, "plugin");
        this.messageService = Objects.requireNonNull(messageService, "messageService");
    }

    @Override
    public String name() {
        return "version";
    }

    @Override
    public String permission() {
        return "raikouhud.command.use";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        messageService.send(sender, MessageKey.COMMAND_VERSION, Map.of("version", plugin.getDescription().getVersion()));
    }
}
