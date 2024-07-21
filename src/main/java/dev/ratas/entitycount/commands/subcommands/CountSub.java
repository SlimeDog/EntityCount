package dev.ratas.entitycount.commands.subcommands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Chunk;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.util.StringUtil;

import dev.ratas.entitycount.config.Messages;
import dev.ratas.slimedogcore.api.commands.SDCCommandOptionSet;
import dev.ratas.slimedogcore.api.messaging.recipient.SDCPlayerRecipient;
import dev.ratas.slimedogcore.api.messaging.recipient.SDCRecipient;
import dev.ratas.slimedogcore.impl.commands.AbstractSubCommand;

/**
 * in-game: entitycount count [ <world> ] [ <entity-type> ]
 *
 * console: entitycount count <world> [ <entity-type> ]​
 */
public class CountSub extends AbstractSubCommand {
    private static final String NAME = "count";
    private static final String USAGE = String.join("\n",
            "/entitycount count [ <world> ] [ <entity-type> ]",
            "/entitycount count world [ <entity-type> ]",
            "/entitycount count chunk [ <entity-type> ]");
    private static final String USAGE_CONSOLE = "/entitycount count <world> [ <entity-type> ]";
    private static final String PERMS = "entitycount.use.count";
    private final Server server;
    private final Messages messages;
    private List<String> worldNames; // lazy initialization
    private List<String> entityTypeNames; // lazy initialization

    public CountSub(Server server, Messages messages) {
        super(NAME, PERMS, USAGE);
        this.server = server;
        this.messages = messages;
    }

    @Override
    public String getUsage(SDCRecipient sender, String[] args) {
        if (sender.isPlayer()) {
            return super.getUsage(sender, args);
        }
        return USAGE_CONSOLE;
    }

    @Override
    public List<String> onTabComplete(SDCRecipient sender, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 1) {
            List<String> opts1 = getWorldNames();
            if (sender.isPlayer()) {
                opts1.add("region");
                opts1.add("chunk");
            }
            return StringUtil.copyPartialMatches(args[0], opts1, list);
        } else if (args.length == 2) {
            return StringUtil.copyPartialMatches(args[1], getEntityTypeNames(), list);
        }
        return list;
    }

    @Override
    public boolean onOptionedCommand(SDCRecipient sender, String[] args, SDCCommandOptionSet opts) {
        LookUpTarget target;
        try {
            target = getSpecifiedWorld(sender, args);
        } catch (WorldNotFoundException e) {
            sender.sendMessage(messages.getNoWorldFound().createWith(e.worldName));
            return true;
        } catch (NoWorldSpecifiedException e) {
            return false;
        }
        EntityType type;
        try {
            type = getSpecifiedEntityType(args);
        } catch (NoEntityTypeFoundException e) {
            sender.sendMessage(messages.getNoEntityTypeFound().createWith(e.name));
            return true;
        }
        EntityCountResults totals = findEntitiesIn(target, type);
        sendTotals(sender, totals, type);
        return true;
    }

    private void sendTotals(SDCRecipient sender, EntityCountResults totals, EntityType targetType) {
        if (targetType != null) {
            int total = totals.counts.getOrDefault(targetType, 0);
            sender.sendMessage(messages.getItem().createWith(targetType, total));
            return;
        }
        List<EntityType> sortedList = new ArrayList<>(totals.counts.keySet());
        sortedList.sort((a, b) -> a.name().compareTo(b.name()));
        sender.sendMessage(messages.getHeader().createWith(totals.total));
        for (EntityType type : sortedList) {
            int val = totals.counts.get(type);
            sender.sendMessage(messages.getItem().createWith(type, val));
        }
    }

    private EntityCountResults findEntitiesIn(LookUpTarget target, EntityType targetType) {
        int total = 0;
        Map<EntityType, Integer> map = new EnumMap<>(EntityType.class);
        for (Entity e : target.getEntities()) {
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

    private LookUpTarget getSpecifiedWorld(SDCRecipient sender, String[] args) {
        if (sender.isPlayer() && args.length < 1) {
            return new WorldTarget(((SDCPlayerRecipient) sender).getLocation().getWorld());
        } else if (sender.isPlayer()) {
            SDCPlayerRecipient player = (SDCPlayerRecipient) sender;
            if (args[0].equalsIgnoreCase("region")) {
                return RegionTarget.fromChunk(player.getLocation().getChunk());
            }
            if (args[0].equalsIgnoreCase("chunk")) {
                return new ChunkTarget(player.getLocation().getChunk());
            }
            World world = server.getWorld(args[0]);
            if (world == null) {
                throw new WorldNotFoundException(args[0]);
            }
            return new WorldTarget(world);
        } else if (args.length < 1) {
            throw new NoWorldSpecifiedException();
        } else {
            World world = server.getWorld(args[0]);
            if (world == null) {
                throw new WorldNotFoundException(args[0]);
            }
            return new WorldTarget(world);
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

    private static interface LookUpTarget {

        List<Entity> getEntities();

    }

    private static class WorldTarget implements LookUpTarget {
        private final World world;

        private WorldTarget(World world) {
            this.world = world;
        }

        @Override
        public List<Entity> getEntities() {
            return world.getEntities();
        }

    }

    private static class ChunkTarget implements LookUpTarget {
        private final Chunk chunk;

        private ChunkTarget(Chunk chunk) {
            this.chunk = chunk;
        }

        @Override
        public List<Entity> getEntities() {
            return Arrays.asList(chunk.getEntities());
        }

    }

    private static class RegionTarget implements LookUpTarget {
        private final World world;
        private final int regionX;
        private final int regionZ;

        private RegionTarget(World world, int regionX, int regionZ) {
            this.world = world;
            this.regionX = regionX;
            this.regionZ = regionZ;
        }

        private List<Chunk> getLoadedChunksInRegion() {
            List<Chunk> chunks = new ArrayList<>();
            for (Chunk chunk : world.getLoadedChunks()) {
                if (chunk.getX() >> 5 == regionX && chunk.getZ() >> 5 == regionZ) {
                    chunks.add(chunk);
                }
            }
            return chunks;
        }

        @Override
        public List<Entity> getEntities() {
            List<Entity> entities = new ArrayList<>();
            for (Chunk chunk : getLoadedChunksInRegion()) {
                entities.addAll(Arrays.asList(chunk.getEntities()));
            }
            return entities;
        }

        private static RegionTarget fromChunk(Chunk chunk) {
            int regionX = chunk.getX() >> 5;
            int regionZ = chunk.getZ() >> 5;
            return new RegionTarget(chunk.getWorld(), regionX, regionZ);
        }
    }

}
