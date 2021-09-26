package dev.ratas.entitycount.config;

import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;

public class Messages extends CustomConfigHandler {
    private static final String NAME = "messages.yml";

    public Messages(JavaPlugin plugin) throws InvalidConfigurationException {
        super(plugin, NAME);
    }

    public String getHeader(int total) {
        return getMessage("header", "Found a total of {total} mobs:").replace("{total}", String.valueOf(total));
    }

    public String getItem(EntityType type, int amount) {
        return getMessage("item", "{type}: {amount}").replace("{type}", type.name()).replace("{amount}",
                String.valueOf(amount));
    }

    public String getNoWorldFound(String name) {
        return getMessage("no-world-found", "World not found: {name}").replace("{name}", name);
    }

    public String getNoEntityTypeFound(String name) {
        return getMessage("no-entity-type-found", "Entity type not found: {name}").replace("{name}", name);
    }

    private String getMessage(String path, String def) {
        return ChatColor.translateAlternateColorCodes('&', getConfig().getString(path, def));
    }

}
