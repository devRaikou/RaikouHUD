package dev.raikou.raikouhud.i18n;

public enum MessageKey {
    PREFIX("prefix"),
    COMMAND_NO_PERMISSION("command.no-permission"),
    COMMAND_PLAYER_ONLY("command.player-only"),
    COMMAND_PLAYER_NOT_FOUND("command.player-not-found"),
    COMMAND_UNKNOWN_SUBCOMMAND("command.unknown-subcommand"),
    COMMAND_HELP_HEADER("command.help.header"),
    COMMAND_HELP_LINE_HELP("command.help.line-help"),
    COMMAND_HELP_LINE_RELOAD("command.help.line-reload"),
    COMMAND_HELP_LINE_STATUS("command.help.line-status"),
    COMMAND_HELP_LINE_TOGGLE("command.help.line-toggle"),
    COMMAND_HELP_LINE_VERSION("command.help.line-version"),
    COMMAND_RELOAD_SUCCESS("command.reload.success"),
    COMMAND_RELOAD_FAILED("command.reload.failed"),
    COMMAND_STATUS_HEADER("command.status.header"),
    COMMAND_STATUS_MODULE_LINE("command.status.module-line"),
    COMMAND_STATUS_PLAYER_LINE("command.status.player-line"),
    COMMAND_TOGGLE_USAGE("command.toggle.usage"),
    COMMAND_TOGGLE_UNKNOWN_MODULE("command.toggle.unknown-module"),
    COMMAND_TOGGLE_TARGET_REQUIRED("command.toggle.target-required"),
    COMMAND_TOGGLE_SUCCESS("command.toggle.success"),
    COMMAND_VERSION("command.version"),
    STATE_ENABLED("state.enabled"),
    STATE_DISABLED("state.disabled"),
    MODULE_SCOREBOARD("module.scoreboard"),
    MODULE_BOSSBAR("module.bossbar"),
    MODULE_ACTIONBAR("module.actionbar"),
    MODULE_TAB("module.tab");

    private final String path;

    MessageKey(String path) {
        this.path = path;
    }

    public String path() {
        return path;
    }
}
