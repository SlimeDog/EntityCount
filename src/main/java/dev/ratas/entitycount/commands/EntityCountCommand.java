package dev.ratas.entitycount.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import dev.ratas.entitycount.config.Messages;

/**
 * in-game: entitycount [ <world> ] [ <entity-type> ]
 *
 * console: entitycount <world> [ <entity-type> ]​
 */
public class EntityCountCommand implements TabExecutor {
    private final Server server;
    private final Messages messages;
    private List<String> worldNames; // lazy initialization
    private List<String> entityTypeNames; // lazy initialization

    public EntityCountCommand(Server server, Messages messages) {
        this.server = server;
        this.messages = messages;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], getWorldNames(), list);
        } else if (args.length == 2) {
            return StringUtil.copyPartialMatches(args[0], getEntityTypeNames(), list);
        }
        return list;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        World world;
        try {
            world = getSpecifiedWorld(sender, args);
        } catch (WorldNotFoundException e) {
            sender.sendMessage(messages.getNoWorldFound(e.worldName));
            return true;
        } catch (NoWorldSpecifiedException e) {
            return false;
        }
        EntityType type;
        try {
            type = getSpecifiedEntityType(args);
        } catch (NoEntityTypeFoundException e) {
            sender.sendMessage(messages.getNoEntityTypeFound(e.name));
            return true;
        }
        EntityCountResults totals = findEntitiesIn(world, type);
        sendTotals(sender, totals, type);
        return true;
    }

    private void sendTotals(CommandSender sender, EntityCountResults totals, EntityType targetType) {
        if (targetType != null) {
            int total = totals.counts.getOrDefault(targetType, 0);
            sender.sendMessage(messages.getItem(targetType, total));
            return;
        }
        List<EntityType> sortedList = new ArrayList<>(totals.counts.keySet());
        sortedList.sort((a, b) -> a.name().compareTo(b.name()));
        sender.sendMessage(messages.getHeader(totals.total));
        for (EntityType type : sortedList) {
            int val = totals.counts.get(type);
            sender.sendMessage(messages.getItem(type, val));
        }
    }

    private EntityCountResults findEntitiesIn(World world, EntityType targetType) {
        int total = 0;
        Map<EntityType, Integer> map = new EnumMap<>(EntityType.class);
        for (Entity e : world.getEntities()) {
            EntityType curType = e.getType();
            if (targetType == null || targetType == curType) {
                int prev = map.getOrDefault(curType, 0);
                map.put(curType, prev + 1);
                total++;
            }
        }
        return new EntityCountResults(map, total);
    }

    private EntityType getSpecifiedEntityType(String[] args) {
        if (args.length < 2) {
            return null; // all
        }
        try {
            return EntityType.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new NoEntityTypeFoundException(args[1]);
        }
    }

    private World getSpecifiedWorld(CommandSender sender, String[] args) {
        if (sender instanceof Player && args.length < 1) {
            return ((Player) sender).getWorld();
        } else if (sender instanceof Player) {
            World world = server.getWorld(args[0]);
            if (world == null) {
                throw new WorldNotFoundException(args[0]);
            }
            return world;
        } else if (args.length < 1) {
            throw new NoWorldSpecifiedException();
        } else {
            World world = server.getWorld(args[0]);
            if (world == null) {
                throw new WorldNotFoundException(args[0]);
            }
            return world;
        }
    }

    private List<String> getWorldNames() {
        if (this.worldNames != null) {
            return this.worldNames;
        }
        this.worldNames = server.getWorlds().stream().map(w -> w.getName()).collect(Collectors.toList());
        return getWorldNames();
    }

    private List<String> getEntityTypeNames() {
        if (this.entityTypeNames != null) {
            return this.entityTypeNames;
        }
        this.entityTypeNames = Arrays.stream(EntityType.values()).map(Enum::name).collect(Collectors.toList());
        return getEntityTypeNames();
    }

    private class WorldNotFoundException extends IllegalArgumentException {
        private final String worldName;

        private WorldNotFoundException(String worldName) {
            super("World not found: " + worldName);
            this.worldName = worldName;
        }

    }

    private class NoWorldSpecifiedException extends IllegalArgumentException {

        private NoWorldSpecifiedException() {
            super("No world specified");
        }

    }

    private class NoEntityTypeFoundException extends IllegalArgumentException {
        private final String name;

        private NoEntityTypeFoundException(String name) {
            super("No entity type found: " + name);
            this.name = name;
        }
    }

    private class EntityCountResults {
        private final Map<EntityType, Integer> counts;
        private final int total;

        private EntityCountResults(Map<EntityType, Integer> counts, int total) {
            this.counts = counts;
            this.total = total;
        }

    }

}
