package dev.ratas.entitycount.commands.subcommands;

import java.util.Collections;
import java.util.List;

import dev.ratas.entitycount.EntityCount;
import dev.ratas.entitycount.config.Messages;
import dev.ratas.slimedogcore.api.commands.SDCCommandOptionSet;
import dev.ratas.slimedogcore.api.messaging.recipient.SDCRecipient;
import dev.ratas.slimedogcore.impl.commands.AbstractSubCommand;

public class ReloadSub extends AbstractSubCommand {
    private static final String NAME = "reload";
    private static final String USAGE = "/entitycount reload";
    private static final String PERMS = "entitycount.use.reload";
    private final EntityCount plugin;
    private final Messages messages;

    public ReloadSub(EntityCount plugin, Messages messages) {
        super(NAME, PERMS, USAGE);
        this.plugin = plugin;
        this.messages = messages;
    }

    @Override
    public List<String> onTabComplete(SDCRecipient sender, String[] args) {
        return Collections.emptyList();
    }

    @Override
    public boolean onOptionedCommand(SDCRecipient sender, String[] args, SDCCommandOptionSet opts) {
        if (plugin.reload()) {
            sender.sendMessage(messages.getRelaoded().getMessage());
        } else {
            sender.sendMessage(messages.getReloadFailed().getMessage());
        }
        return true;
    }

}
