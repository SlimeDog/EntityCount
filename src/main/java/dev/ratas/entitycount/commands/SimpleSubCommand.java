package dev.ratas.entitycount.commands;

import java.util.List;

import org.bukkit.command.CommandSender;

public abstract class SimpleSubCommand {
    private final String name;
    private final String usage;
    private final String perms;
    private final boolean needsPlayer;

    public SimpleSubCommand(String name, String usage, String perms) {
        this(name, usage, perms, false);
    }

    public SimpleSubCommand(String name, String usage, String perms, boolean needsPlayer) {
        this.name = name;
        this.usage = usage;
        this.perms = perms;
        this.needsPlayer = needsPlayer;
    }

    public String getName() {
        return name;
    }

    public String getUsage(CommandSender sender, String[] args) {
        return usage;
    }

    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission(perms);
    }

    public boolean showTabCompletion() {
        return true;
    }

    public boolean needsPlayer() {
        return needsPlayer;
    }

    public abstract List<String> getTabComletions(CommandSender sender, String[] args);

    public abstract boolean executeCommand(CommandSender sender, String[] args);

}
