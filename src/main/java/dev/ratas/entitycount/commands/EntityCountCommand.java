package dev.ratas.entitycount.commands;

import org.bukkit.Server;

import dev.ratas.entitycount.EntityCount;
import dev.ratas.entitycount.commands.subcommands.CountSub;
import dev.ratas.entitycount.commands.subcommands.ReloadSub;
import dev.ratas.entitycount.config.Messages;

public class EntityCountCommand extends AbstractParentCommand {

    public EntityCountCommand(EntityCount plugin, Server server, Messages messages) {
        super(generateSubCommands(plugin, server, messages));
    }

    private static SimpleSubCommand[] generateSubCommands(EntityCount plugin, Server server, Messages messages) {
        return new SimpleSubCommand[] { new CountSub(server, messages), new ReloadSub(plugin, messages) };
    }

}
