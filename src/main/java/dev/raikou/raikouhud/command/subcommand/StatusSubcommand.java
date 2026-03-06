package dev.raikou.raikouhud.command.subcommand;

import dev.raikou.raikouhud.command.permission.PermissionNode;
import dev.raikou.raikouhud.hud.HudModule;
import dev.raikou.raikouhud.hud.HudModuleType;
import dev.raikou.raikouhud.hud.HudService;
import dev.raikou.raikouhud.i18n.MessageKey;
import dev.raikou.raikouhud.i18n.MessageService;
import dev.raikou.raikouhud.player.PlayerSessionService;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class StatusSubcommand implements Subcommand {

    private final MessageService messageService;
    private final HudService hudService;
    private final PlayerSessionService sessionService;

    public StatusSubcommand(MessageService messageService, HudService hudService, PlayerSessionService sessionService) {
        this.messageService = Objects.requireNonNull(messageService, "messageService");
        this.hudService = Objects.requireNonNull(hudService, "hudService");
        this.sessionService = Objects.requireNonNull(sessionService, "sessionService");
    }

    @Override
    public String name() {
        return "status";
    }

    @Override
    public String permission() {
        return PermissionNode.STATUS.node();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player target = null;
        if (args.length >= 1) {
            if (!sender.hasPermission(PermissionNode.STATUS_OTHERS.node())) {
                messageService.send(sender, MessageKey.COMMAND_NO_PERMISSION);
                return;
            }
            target = Bukkit.getPlayerExact(args[0]);
            if (target == null) {
                messageService.send(sender, MessageKey.COMMAND_PLAYER_NOT_FOUND, Map.of("player", args[0]));
                return;
            }
        } else if (sender instanceof Player player) {
            target = player;
        }

        messageService.sendRaw(sender, MessageKey.COMMAND_STATUS_HEADER, Map.of("locale", messageService.activeLocaleCode()));
        for (HudModuleType type : HudModuleType.values()) {
            HudModule module = hudService.module(type);
            boolean globalEnabled = module != null && module.isGloballyEnabled();
            messageService.sendRaw(sender, MessageKey.COMMAND_STATUS_MODULE_LINE, Map.of(
                "module", moduleName(type),
                "state", state(globalEnabled)
            ));

            if (target != null) {
                boolean playerEnabled = sessionService.session(target.getUniqueId())
                    .map(session -> session.isModuleEnabled(type))
                    .orElse(true);
                messageService.sendRaw(sender, MessageKey.COMMAND_STATUS_PLAYER_LINE, Map.of(
                    "player", target.getName(),
                    "module", moduleName(type),
                    "state", state(playerEnabled)
                ));
            }
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1 && sender.hasPermission(PermissionNode.STATUS_OTHERS.node())) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
        }
        return List.of();
    }

    private String moduleName(HudModuleType type) {
        return switch (type) {
            case SCOREBOARD -> messageService.render(MessageKey.MODULE_SCOREBOARD);
            case BOSSBAR -> messageService.render(MessageKey.MODULE_BOSSBAR);
            case ACTIONBAR -> messageService.render(MessageKey.MODULE_ACTIONBAR);
            case TAB -> messageService.render(MessageKey.MODULE_TAB);
        };
    }

    private String state(boolean enabled) {
        return enabled
            ? messageService.render(MessageKey.STATE_ENABLED)
            : messageService.render(MessageKey.STATE_DISABLED);
    }
}

