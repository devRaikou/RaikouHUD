package dev.raikou.raikouhud.command.subcommand;

import dev.raikou.raikouhud.i18n.MessageKey;
import dev.raikou.raikouhud.i18n.MessageService;
import java.util.Objects;
import org.bukkit.command.CommandSender;

public final class ReloadSubcommand implements Subcommand {

    private final MessageService messageService;
    private final Runnable reloadAction;

    public ReloadSubcommand(MessageService messageService, Runnable reloadAction) {
        this.messageService = Objects.requireNonNull(messageService, "messageService");
        this.reloadAction = Objects.requireNonNull(reloadAction, "reloadAction");
    }

    @Override
    public String name() {
        return "reload";
    }

    @Override
    public String permission() {
        return "raikouhud.command.reload";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        try {
            reloadAction.run();
            messageService.send(sender, MessageKey.COMMAND_RELOAD_SUCCESS);
        } catch (Exception exception) {
            messageService.send(sender, MessageKey.COMMAND_RELOAD_FAILED);
        }
    }
}

