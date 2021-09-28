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

    public String getRunningLatestVersion() {
        return getMessage("update-running-latest-version", "Running latest version of EntityCount");
    }

    public String getNewVersionAvailable(String version) {
        return getMessage("update-new-version-available", "A new version of EntityCount is available: {version}")
                .replace("{version}", version);
    }

    public String getUpdateInfoUnavailable() {
        return getMessage("update-info-unavailable", "Update info is not available at this time");
    }

    public String getRelaoded() {
        return getMessage("reloaded", "Successfully reloaded");
    }

    public String getReloadFailed() {
        return getMessage("reload-failed", "Failed to reload. Shutting down plugin.");
    }

    private String getMessage(String path, String def) {
        return ChatColor.translateAlternateColorCodes('&', getConfig().getString(path, def));
    }

}
