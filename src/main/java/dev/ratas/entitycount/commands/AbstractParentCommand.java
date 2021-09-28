package dev.ratas.entitycount.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

public abstract class AbstractParentCommand implements TabExecutor {
    private final Map<String, SimpleSubCommand> commands = new LinkedHashMap<>();

    protected AbstractParentCommand(SimpleSubCommand... cmds) {
        for (SimpleSubCommand cmd : cmds) {
            addSubCommand(cmd);
        }
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 1) {
            List<String> subs = new ArrayList<>();
            for (SimpleSubCommand sc : commands.values()) {
                if (sc.hasPermission(sender) && (!sc.needsPlayer() || sender instanceof Player)) {
                    subs.add(sc.getName());
                }
            }
            return StringUtil.copyPartialMatches(args[0], subs, list);
        }
        SimpleSubCommand sub = getSimpleSubCommand(args[0]);
        if (sub == null || !sub.hasPermission(sender) || (sub.needsPlayer() && !(sender instanceof Player)))
            return list;
        return sub.getTabComletions(sender, Arrays.copyOfRange(args, 1, args.length));
    }

    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(getUsage(sender, args));
            return true;
        }
        SimpleSubCommand sub = getSimpleSubCommand(args[0]);
        if (sub == null || !sub.hasPermission(sender)) {
            sender.sendMessage(getUsage(sender, args));
            return true;
        }
        if (sub.needsPlayer() && !(sender instanceof Player)) {
            sender.sendMessage("This command can only be used in game!");
            return true;
        }
        if (!sub.executeCommand(sender, Arrays.copyOfRange(args, 1, args.length))) {
            sender.sendMessage(sub.getUsage(sender, args));
        }
        return true;
    }

    public void addSubCommand(SimpleSubCommand cmd) {
        commands.put(cmd.getName().toLowerCase(), cmd);
    }

    public SimpleSubCommand removeSimpleSubCommand(String name) {
        return commands.remove(name.toLowerCase());
    }

    public SimpleSubCommand getSimpleSubCommand(String name) {
        return commands.get(name.toLowerCase());
    }

    public List<SimpleSubCommand> getSimpleSubCommands() {
        return new ArrayList<>(commands.values());
    }

    public String getUsage(CommandSender sender, String[] args) {
        StringBuilder builder = new StringBuilder();
        for (SimpleSubCommand sub : getSimpleSubCommands()) {
            if (sub.hasPermission(sender)) {
                if (builder.length() > 0) {
                    builder.append("\n");
                }
                builder.append(sub.getUsage(sender, args));
            }
        }
        return builder.toString();
    }

}
