package dev.raikou.raikouhud.command.subcommand;

import dev.raikou.raikouhud.command.permission.PermissionNode;
import dev.raikou.raikouhud.hud.HudModule;
import dev.raikou.raikouhud.hud.HudModuleType;
import dev.raikou.raikouhud.hud.HudService;
import dev.raikou.raikouhud.i18n.MessageKey;
import dev.raikou.raikouhud.i18n.MessageService;
import dev.raikou.raikouhud.player.PlayerSession;
import dev.raikou.raikouhud.player.PlayerSessionService;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class ToggleSubcommand implements Subcommand {

    private final MessageService messageService;
    private final PlayerSessionService sessionService;
    private final HudService hudService;

    public ToggleSubcommand(MessageService messageService, PlayerSessionService sessionService, HudService hudService) {
        this.messageService = Objects.requireNonNull(messageService, "messageService");
        this.sessionService = Objects.requireNonNull(sessionService, "sessionService");
        this.hudService = Objects.requireNonNull(hudService, "hudService");
    }

    @Override
    public String name() {
        return "toggle";
    }

    @Override
    public String permission() {
        return PermissionNode.TOGGLE.node();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 1) {
            messageService.send(sender, MessageKey.COMMAND_TOGGLE_USAGE);
            return;
        }

        Optional<HudModuleType> maybeModule = HudModuleType.fromInput(args[0]);
        if (maybeModule.isEmpty()) {
            messageService.send(sender, MessageKey.COMMAND_TOGGLE_UNKNOWN_MODULE, Map.of("module", args[0]));
            return;
        }
        HudModuleType moduleType = maybeModule.get();

        Player target;
        if (args.length >= 2) {
            if (!sender.hasPermission(PermissionNode.TOGGLE_OTHERS.node())) {
                messageService.send(sender, MessageKey.COMMAND_NO_PERMISSION);
                return;
            }
            target = Bukkit.getPlayerExact(args[1]);
            if (target == null) {
                messageService.send(sender, MessageKey.COMMAND_PLAYER_NOT_FOUND, Map.of("player", args[1]));
                return;
            }
        } else if (sender instanceof Player player) {
            target = player;
        } else {
            messageService.send(sender, MessageKey.COMMAND_TOGGLE_TARGET_REQUIRED);
            return;
        }

        PlayerSession session = sessionService.create(target.getUniqueId());
        boolean enabled = session.toggleModule(moduleType);
        session.markDirty();

        HudModule module = hudService.module(moduleType);
        if (module != null) {
            if (enabled) {
                module.onPlayerJoin(session);
            } else {
                module.onPlayerQuit(target.getUniqueId());
            }
        }

        messageService.send(sender, MessageKey.COMMAND_TOGGLE_SUCCESS, Map.of(
            "module", moduleName(moduleType),
            "player", target.getName(),
            "state", enabled ? messageService.render(MessageKey.STATE_ENABLED) : messageService.render(MessageKey.STATE_DISABLED)
        ));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return List.of("scoreboard", "bossbar", "actionbar", "tab");
        }
        if (args.length == 2 && sender.hasPermission(PermissionNode.TOGGLE_OTHERS.node())) {
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
}

