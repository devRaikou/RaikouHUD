package dev.raikou.raikouhud.command.subcommand;

import dev.raikou.raikouhud.i18n.MessageKey;
import dev.raikou.raikouhud.i18n.MessageService;
import java.util.Objects;
import org.bukkit.command.CommandSender;

public final class HelpSubcommand implements Subcommand {

    private final MessageService messageService;

    public HelpSubcommand(MessageService messageService) {
        this.messageService = Objects.requireNonNull(messageService, "messageService");
    }

    @Override
    public String name() {
        return "help";
    }

    @Override
    public String permission() {
        return "raikouhud.command.use";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        messageService.sendRaw(sender, MessageKey.COMMAND_HELP_HEADER);
        messageService.sendRaw(sender, MessageKey.COMMAND_HELP_LINE_HELP);
        messageService.sendRaw(sender, MessageKey.COMMAND_HELP_LINE_RELOAD);
        messageService.sendRaw(sender, MessageKey.COMMAND_HELP_LINE_STATUS);
        messageService.sendRaw(sender, MessageKey.COMMAND_HELP_LINE_TOGGLE);
        messageService.sendRaw(sender, MessageKey.COMMAND_HELP_LINE_VERSION);
    }
}

