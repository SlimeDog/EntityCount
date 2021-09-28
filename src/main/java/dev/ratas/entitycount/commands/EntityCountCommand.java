package dev.ratas.entitycount.commands;

import org.bukkit.Server;

import dev.ratas.entitycount.commands.subcommands.CountSub;
import dev.ratas.entitycount.config.Messages;

public class EntityCountCommand extends AbstractParentCommand {

    public EntityCountCommand(Server server, Messages messages) {
        super(generateSubCommands(server, messages));
    }

    private static SimpleSubCommand[] generateSubCommands(Server server, Messages messages) {
        return new SimpleSubCommand[] { new CountSub(server, messages) };
    }

}
