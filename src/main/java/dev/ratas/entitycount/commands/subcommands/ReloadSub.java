package dev.ratas.entitycount.commands.subcommands;

import java.util.Collections;
import java.util.List;

import org.bukkit.command.CommandSender;

import dev.ratas.entitycount.EntityCount;
import dev.ratas.entitycount.commands.SimpleSubCommand;
import dev.ratas.entitycount.config.Messages;

public class ReloadSub extends SimpleSubCommand {
    private static final String NAME = "reload";
    private static final String USAGE = "/entitycount reload";
    private static final String PERMS = "entitycount.use.reload";
    private final EntityCount plugin;
    private final Messages messages;

    public ReloadSub(EntityCount plugin, Messages messages) {
        super(NAME, USAGE, PERMS);
        this.plugin = plugin;
        this.messages = messages;
    }

    @Override
    public List<String> getTabComletions(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

    @Override
    public boolean executeCommand(CommandSender sender, String[] args) {
        if (plugin.reload()) {
            sender.sendMessage(messages.getRelaoded());
        } else {
            sender.sendMessage(messages.getReloadFailed());
        }
        return true;
    }

}
