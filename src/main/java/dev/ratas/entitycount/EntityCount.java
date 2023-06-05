package dev.ratas.entitycount;

import java.util.function.BiConsumer;

import org.bstats.bukkit.Metrics;
import org.bukkit.configuration.InvalidConfigurationException;

import dev.ratas.entitycount.commands.EntityCountCommand;
import dev.ratas.entitycount.config.Messages;

import dev.ratas.slimedogcore.impl.SlimeDogCore;
import dev.ratas.slimedogcore.impl.utils.UpdateChecker;

public class EntityCount extends SlimeDogCore {
    private static final int SPIGOT_ID = 96546;
    private static final String HANGAR_AUTHOR = "SlimeDog";
    private static final String HANGAR_SLUG = "EntityCount";
    private Messages messages;

    @Override
    public void pluginEnabled() {
        saveDefaultConfig();
        try {
            messages = new Messages(this);
        } catch (InvalidConfigurationException e) {
            shutDownWith(e);
            return;
        }
        getCommand("entitycount").setExecutor(new EntityCountCommand(this, getServer(), messages));
        if (getConfig().getBoolean("enable-metrics", true)) {
            new Metrics(this, 12888);
        }
        // update
        if (getConfig().getBoolean("check-for-updates", true)) {
            String updateSource = getConfig().getString("update-source", "Hangar");
            BiConsumer<UpdateChecker.VersionResponse, String> consumer = (response, version) -> {
                switch (response) {
                    case LATEST:
                        getLogger().info(messages.getRunningLatestVersion().getMessage().getFilled());
                        break;
                    case FOUND_NEW:
                        getLogger().info(messages.getNewVersionAvailable().createWith(version).getFilled());
                        break;
                    case UNAVAILABLE:
                        getLogger().info(messages.getUpdateInfoUnavailable().getMessage().getFilled());
                        break;
                }
            };
            UpdateChecker checker;
            if (updateSource.equalsIgnoreCase("SpigotMC")) {
                checker = UpdateChecker.forSpigot(this, consumer, SPIGOT_ID);
            } else {
                checker = UpdateChecker.forHangar(this, consumer, HANGAR_AUTHOR, HANGAR_SLUG);
            }
            checker.check();
        }
    }

    @Override
    public void pluginDisabled() {
    }

    private void shutDownWith(Throwable e) {
        getLogger().warning("Problem while running the plugin. Need to shut down: " + e.getMessage());
        e.printStackTrace();
        getServer().getPluginManager().disablePlugin(this);
    }

    public boolean reload() {
        reloadConfig();
        try {
            messages.reloadConfig();
            messages.reload();
        } catch (RuntimeException e) {
            shutDownWith(e);
            return false;
        }
        getDefaultConfig().reload();
        return true;
    }

}
