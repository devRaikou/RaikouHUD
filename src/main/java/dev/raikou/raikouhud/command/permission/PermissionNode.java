package dev.raikou.raikouhud.command.permission;

public enum PermissionNode {
    USE("raikouhud.command.use"),
    RELOAD("raikouhud.command.reload"),
    STATUS("raikouhud.command.status"),
    STATUS_OTHERS("raikouhud.command.status.others"),
    TOGGLE("raikouhud.command.toggle"),
    TOGGLE_OTHERS("raikouhud.command.toggle.others"),
    ADMIN("raikouhud.admin");

    private final String node;

    PermissionNode(String node) {
        this.node = node;
    }

    public String node() {
        return node;
    }
}

