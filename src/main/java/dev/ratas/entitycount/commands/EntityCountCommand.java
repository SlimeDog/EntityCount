package dev.ratas.entitycount.commands;

import org.bukkit.Server;

import dev.ratas.entitycount.EntityCount;
import dev.ratas.entitycount.commands.subcommands.CountSub;
import dev.ratas.entitycount.commands.subcommands.ReloadSub;
import dev.ratas.entitycount.config.Messages;
import dev.ratas.slimedogcore.api.commands.SDCSubCommand;
import dev.ratas.slimedogcore.impl.commands.BukkitFacingParentCommand;

public class EntityCountCommand extends BukkitFacingParentCommand {

    public EntityCountCommand(EntityCount plugin, Server server, Messages messages) {
        for (SDCSubCommand sc : generateSubCommands(plugin, server, messages)) {
            this.addSubCommand(sc);
        }
    }

    private static SDCSubCommand[] generateSubCommands(EntityCount plugin, Server server, Messages messages) {
        return new SDCSubCommand[] { new CountSub(server, messages), new ReloadSub(plugin, messages) };
    }

}
